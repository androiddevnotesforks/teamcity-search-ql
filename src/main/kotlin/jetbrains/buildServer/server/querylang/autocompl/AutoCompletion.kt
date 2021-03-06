package jetbrains.buildServer.server.querylang.autocompl

import jetbrains.buildServer.server.querylang.ast.FullQuery
import jetbrains.buildServer.server.querylang.ast.PartialQuery
import jetbrains.buildServer.server.querylang.parser.ParsingException
import jetbrains.buildServer.server.querylang.parser.QLangGrammarLexer
import jetbrains.buildServer.server.querylang.parser.QLangGrammarParser
import jetbrains.buildServer.server.querylang.parser.QueryParser
import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.TeamCityProperties
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ErrorNodeImpl
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.lang.IllegalStateException

class AutoCompletion(
    val projectManager: ProjectManager? = null,
    private val compl: Completer
) {
    private val AUTOCOMPLETION_LIMIT_NAME = "teamcity.internal.searchQL.autocompletion.maxSuggested"
    private val parser = QueryParser

    val limit = TeamCityProperties.getInteger(AUTOCOMPLETION_LIMIT_NAME, 100)

    fun complete(input: String): List<CompletionResult> {
        val stream = CharStreams.fromString(input)
        val lexer = QLangGrammarLexer(stream)
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parserTree = QLangGrammarParser(tokens)
        parserTree.removeErrorListeners()

        val start = parserTree.start() ?: return emptyList()
        if (start.children == null) {
            return emptyList()
        }

        val treeFindNode = start.find()
        val treePartNode = start.partialQuery()

        //autocompletion
        if (treeFindNode != null) {
            if (input.last() == '?') {
                val res = tryCompleteCollectorQuery(input)
                if (res != null) {
                    return res.map { CompletionResult(input.dropLast(1) + it, it) }.sortedWith(compareBy({ it.show.length }, {it.show})).take(limit)
                }
            }

            val (word, objectTypes, trace, completeModifier) = getFilterTrace(treeFindNode, input) ?: return emptyList()
            val vars = compl.suggest(input.dropLast(word.length), objectTypes, trace, word, completeModifier, limit)
            return vars
        }

        //suggest full queries for partial query
        if (treePartNode != null) {
            try {
                val multipleQueries = parser.parse(input) as? PartialQuery ?: return emptyList()
                return multipleQueries.fullQueries.map {it.createStr()}.map {CompletionResult(it, it)}
            } catch (e: ParsingException) {
                return completePartialQuery(input, treePartNode)
            }
        }
        return emptyList()
    }


    private fun getFilterTrace(rootNode: ParserRuleContext, input: String): Trace? {
        var node: ParseTree = rootNode

        //the filters sequence before the word we want to complete
        val trace = mutableListOf<String>()

        //index of the last token, that was parsed successfully
        var lastParsedIndex = 0

        //index of the last filter name, that was parsed
        var lastFilterIndex = -1

        //the list of object types that we want to find
        var objectTypes = listOf<String>()

        var completeModifier = false
        loop@ while (true) {
            if (node is QLangGrammarParser.FindContext) {
                if (node.childCount < 1) {
                    return null
                }

                //there is not `find` keyword
                if (node.children.size == 1) {
                    return null
                }

                val conditionInSubproject = node.conditionInSubproject() ?: return null

                //there is no `with` keyword
                // complete object name
                if (conditionInSubproject.childCount == 0) {
                    val objectType = getLastObjectType(node.multipleObjects(), input) ?: return null
                    return Trace(objectType)
                }

                //if `with` keyword wasn't finished,
                //or `with` keyword is the last word in input.
                if (conditionInSubproject.childCount == 1
                    || (conditionInSubproject.getChild(0) as TerminalNode).symbol.stopIndex == input.lastIndex) {
                    return null
                }

                //if the `with` keyword was finished, we can collect object types that we want to search
                objectTypes = node.multipleObjects().objectKeyword().map {it.text}
            }

            //add filter name to `trace`
            if (node is QLangGrammarParser.FilterContext) {
                val child = node.getChild(0)
                trace.add(
                        when (child) {
                            is ParserRuleContext -> child.start.text
                            else -> throw IllegalStateException("Unknown filter")
                        }
                )
            }

            //if the node has no children, than we reach the end of the input
            if (node.childCount == 0) {
                break
            }

            if (node is QLangGrammarParser.ParamStringFilterCaseContext) {
                break
            }

            when (node) {
                is QLangGrammarParser.ConditionInSubprojectContext -> {
                    //when there is no whitespace after `with` keyword
                    if (getIndex(node, 0) == input.lastIndex) {
                        return null
                    }
                    lastParsedIndex = getLastIndex(node)
                    node = node.getChild(node.childCount - 1)
                }
                is QLangGrammarParser.ConditionBracesContext -> {
                    lastParsedIndex = getIndex(node, 0)
                    node = node.condition()
                }
                is QLangGrammarParser.MultFilterContext -> {
                    lastParsedIndex = getIndex(node, 0)
                    node = node.condition()
                }
                else -> {
                    val n = node.childCount

                    if (node.parent is QLangGrammarParser.FilterContext) {
                        lastFilterIndex = getIndex(node, 0)
                        val modifierListNode = getModifierListNode(node)
                        if (modifierListNode != null && modifierListNode.stop.text != "]") {
                            node = modifierListNode
                            completeModifier = true
                            continue@loop
                        }

                        if (!(node.getChild(n - 1) is QLangGrammarParser.FilterOrConditionContext)) {
                            //get the last index of the filter name
                            lastParsedIndex = getIndex(node, 0)

                            //then this filter doesn't have nested filter and
                            //there is no point to go down the tree
                            break@loop
                        }
                    }

                    if (n - 2 >= 0) {
                        lastParsedIndex = getLastIndex(node)
                    }
                    node = node.getChild(n - 1)
                }
            }
        }

        val lastWord = input.substring(lastParsedIndex + 1).trimStart()
        if (lastWord.isEmpty()) {

            //if `lastWord` is empty then we want to complete empty string
            //but if the last symbol is not ' ' or '(' then
            //we want to complete the last word in `trace` not to start the new one.
            if (input.last() !in listOf(' ', '(', ')', '[', ']')) {
                if (lastFilterIndex == input.lastIndex) {
                    if (trace.size == 0) {
                        return null
                    }
                    return Trace(trace.last(), objectTypes, trace.dropLast(1), completeModifier)
                }
                return null
            }
        }

        return Trace(lastWord, objectTypes, trace, completeModifier)
    }

    private fun completePartialQuery(input: String, rootNode: QLangGrammarParser.PartialQueryContext): List<CompletionResult> {
        val trace = getFilterTrace(rootNode, input) ?: return listOf()
        return if (trace.trace.isEmpty()) {
            val flFilters = getFirstLevelFilters(rootNode.condition())
            compl.suggestBasedOnOther(input.dropLast(trace.word.length), flFilters, trace.word)
        } else {
            compl.suggestForPartial(input.dropLast(trace.word.length), trace.trace, trace.word)
        }
    }

    private fun getFirstLevelFilters(rootNode: ParserRuleContext?): List<String> {
        if (rootNode == null) {
            return listOf()
        }
        val node = rootNode
        when (node) {
            is QLangGrammarParser.ConditionAndContext -> {
                return getFirstLevelFilters(node.condition(0)) + getFirstLevelFilters(node.condition(1))
            }
            is QLangGrammarParser.ConditionOrContext -> {
                return getFirstLevelFilters(node.condition(0)) + getFirstLevelFilters(node.condition(1))
            }
            is QLangGrammarParser.ConditionNotContext -> {
                return getFirstLevelFilters(node.condition())
            }
            is QLangGrammarParser.ConditionFilterContext -> {
                node.filter()?.let { return listOf(it.start.text) }
            }
        }
        return emptyList()
    }

    private fun getLastIndex(node: ParseTree): Int {
        return getIndex(node, node.childCount - 2)
    }

    private fun getIndex(node: ParseTree, i: Int): Int {
        val child = node.getChild(i)
        return when (child) {
            is ParserRuleContext -> child.stop.stopIndex
            is TerminalNode -> child.symbol.stopIndex
            else -> throw IllegalStateException("Unknown node type")
        }
    }

    private fun getModifierListNode(node: ParseTree): QLangGrammarParser.ModifierListContext? {
        for (i in 0..node.childCount) {
            val ch = node.getChild(i)
            if (ch is QLangGrammarParser.ModifierListContext) {
                return ch
            }
        }
        return null
    }

    private fun hasErorNodeInSubtree(node: ParseTree): Boolean {
        if (node is ErrorNodeImpl) {
            return true
        }
        var i = 0
        while (i < node.childCount) {
            if (hasErorNodeInSubtree(node.getChild(i))) {
                return true
            }
            i += 1
        }
        return false
    }

    private fun getLastObjectType(node: QLangGrammarParser.MultipleObjectsContext, input: String): String? {
        val child = node.getChild(node.childCount - 1)
        if (!hasErorNodeInSubtree(child) && child.childCount > 0) {
            //if input finishes with this word, then we want to complete it
            val lastIndex = getIndex(node, node.childCount - 1)
            if (lastIndex == input.lastIndex) {

                //the input finishes with `find` keyword without a whitespace
                if (child.text == "" && input.last() !in listOf(' ', ',')) {
                    return null
                }

                return child.text
            }

            //there is something that parser doesn't recognize(e.g. whitespace)
            //nothing to complete
            return null
        }
        if (input.trimStart().endsWith("find")) {
            return null
        }
        return child.text
    }

    private fun tryCompleteCollectorQuery(input: String): List<String>? {
        val res = QueryParser.parseWithErrors(input, true) as? FullQuery
        return res?.getPossibleStrings()
    }

    data class Trace(
        val word: String,
        val objectTypes: List<String>? = null,
        val trace: List<String> = emptyList(),
        val completeModifier: Boolean = false
    )
}