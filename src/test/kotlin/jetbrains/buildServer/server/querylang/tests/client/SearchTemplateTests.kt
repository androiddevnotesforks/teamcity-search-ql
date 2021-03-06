package jetbrains.buildServer.server.querylang.tests.client

import jetbrains.buildServer.server.querylang.parser.QueryParser
import jetbrains.buildServer.server.querylang.tests.BaseQueryLangTest
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import kotlin.test.assertFailsWith

class SearchTemplateTests : BaseQueryLangTest() {
    @BeforeMethod
    override fun setUp() {
        super.setUp()

        TProject("BaseProject",
            TParam("param1", "qwerty"),
            TTemplate("test1",
                TOption("abc", "bcd"),
                TParam("path", "abacaba")
            ).bind("b1"),
            TTemplate("test2",
                TParam("path", "abadaba"),
                TParam("resolvedParam1", "%param1%")
            ).bind("b2"),
            TTemplate("Template1").bind("b3"),

            TProject("Project1",
                TTemplate("temp5").bind("b4")
            )
        ).create()
    }

    @DataProvider(name = "data")
    fun dataProvider() = TestDataProvider()
        .addTempCase(
            "find template with name test1",
            "b1"
        )
        .addTempCase(
            "find template with param[resolved] * = qwerty",
            "b2"
        )
        .addTempCase(
            """ find template with param * = "%param1%" """,
            "b2"
        )
        .addTempCase(
            """ find template with val[resolved] qwerty""",
            "b2"
        )
        .end()

    @DataProvider(name = "compl")
    fun complData() = TestDataProvider()
        .addComplCase(
            "find template with option ab",
            "abc"
        )
        .addComplCase(
            "find template with option abc=",
            "abc=bcd"
        )
        .addComplCase(
            "find template with param pa",
            "path"
        )
        .addComplCase(
            "find template with param path=aba",
            "path=abacaba", "path=abadaba"
        )
        .addComplCase(
            "find template with na",
            "name"
        )
        .addComplCase(
            "find template with name Templ",
            "Template1"
        )
        .end()

    @DataProvider(name = "failed")
    fun failedData() = TestFailedDataProvdier()

        .end()

    @DataProvider(name = "eval")
    fun evalData() = TestDataProvider()
        .addNoneEvalCase(
            "find template with id temp5",
            "temp5"
        )
        .addNoneEvalCase(
            "find template with parent id Project1",
            "temp5"
        )
        .end()


    @Test(dataProvider = "data")
    fun parametrizedTest(query: String, expected: List<String>) {
        val actual = getIds(query)

        assertEquals(expected.sorted(), actual)
    }

    @Test(dataProvider = "failed")
    fun parametrizedFailedParsingTest(query: String, exc: Class<out Exception>) {
        assertFailsWith(exc.kotlin) { QueryParser.parse(query)}
    }

    @Test(dataProvider = "compl")
    fun parametrizedCompletionTests(query: String, expected: List<String>) {
        waitForIndexing()
        checkVars(query, expected)
    }

    @Test(dataProvider = "eval")
    fun evalTest(query: String, expected: List<String>) {
        waitForIndexing()
        checkEval(query, expected)
    }
}