// Generated from QLangGrammar.g4 by ANTLR 4.8

    package jetbrains.buildServer.server.querylang.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QLangGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QLangGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(QLangGrammarParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd(QLangGrammarParser.AndContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr(QLangGrammarParser.OrContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot(QLangGrammarParser.NotContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#objectId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectId(QLangGrammarParser.ObjectIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#objectType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectType(QLangGrammarParser.ObjectTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#parameterValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterValue(QLangGrammarParser.ParameterValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#find}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFind(QLangGrammarParser.FindContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#fbuildConf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFbuildConf(QLangGrammarParser.FbuildConfContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#fvcsRoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFvcsRoot(QLangGrammarParser.FvcsRootContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#fproject}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFproject(QLangGrammarParser.FprojectContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#ftemplate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFtemplate(QLangGrammarParser.FtemplateContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#fbuildConfOrTemp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFbuildConfOrTemp(QLangGrammarParser.FbuildConfOrTempContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#conditionInSubproject}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionInSubproject(QLangGrammarParser.ConditionInSubprojectContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#filter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilter(QLangGrammarParser.FilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionBraces}
	 * labeled alternative in {@link QLangGrammarParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionBraces(QLangGrammarParser.ConditionBracesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionNot}
	 * labeled alternative in {@link QLangGrammarParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionNot(QLangGrammarParser.ConditionNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionFilter}
	 * labeled alternative in {@link QLangGrammarParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionFilter(QLangGrammarParser.ConditionFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionAnd}
	 * labeled alternative in {@link QLangGrammarParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionAnd(QLangGrammarParser.ConditionAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionOr}
	 * labeled alternative in {@link QLangGrammarParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionOr(QLangGrammarParser.ConditionOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleFilter}
	 * labeled alternative in {@link QLangGrammarParser#filterOrCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleFilter(QLangGrammarParser.SingleFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multFilter}
	 * labeled alternative in {@link QLangGrammarParser#filterOrCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultFilter(QLangGrammarParser.MultFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#idFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdFilter(QLangGrammarParser.IdFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#projectFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProjectFilter(QLangGrammarParser.ProjectFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#parentFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentFilter(QLangGrammarParser.ParentFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#triggerFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTriggerFilter(QLangGrammarParser.TriggerFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#stepFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepFilter(QLangGrammarParser.StepFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#featureFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFeatureFilter(QLangGrammarParser.FeatureFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#typeFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeFilter(QLangGrammarParser.TypeFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#parameterFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterFilter(QLangGrammarParser.ParameterFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#parValueFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParValueFilter(QLangGrammarParser.ParValueFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#enabledFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnabledFilter(QLangGrammarParser.EnabledFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#ancestorFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAncestorFilter(QLangGrammarParser.AncestorFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#ancestorOrSelfFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAncestorOrSelfFilter(QLangGrammarParser.AncestorOrSelfFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLangGrammarParser#templateDepFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateDepFilter(QLangGrammarParser.TemplateDepFilterContext ctx);
}