package jetbrains.buildServer.server.querylang.tests.client

import jetbrains.buildServer.server.querylang.parser.QueryParser
import jetbrains.buildServer.server.querylang.tests.BaseQueryLangTest
import org.testng.annotations.*
import java.lang.Thread.sleep
import kotlin.test.assertFailsWith

class FilterDependencyClientTests : BaseQueryLangTest() {

    @BeforeMethod
    override fun setUp() {
        super.setUp()

        TProject("BaseProject",
            TBuildConf("btest1").bind("b1"),

            TProject("Project2",
                TBuildConf("btest3").bind("b3")
            ),

            TBuildConf("btest2",
                TSDependency("b3")
            ).bind("b2"),

            TBuildConf("btest4",
                TADependency("b2", "qwerqwreqerq")
            ).bind("b4"),

            TTemplate("temp1",
                TSDependency("b4",
                    TOption("opt1", "bcd")
                )
            ).bind("t1"),

            TTemplate("temp2",
                TADependency("b4", "abacabadaba")
            ).bind("t2"),

            TTemplate("temp3",
                TSDependency("b3",
                    TOption("opt1", "abc")
                )
            ).bind("t3"),

            TBuildConf("test5",
                TTempDependency("t1"),
                TADependency("b3", "zxcvzxcvzc")
            ).bind("b5"),

            TBuildConf("test6",
                TADependency("b4", "irotiirtroi")
            ).bind("b6")
        ).create(true)
        
    }

    @DataProvider(name = "data")
    fun dataProvider() = TestDataProvider()
        .addBCCase(
            "find buildConfiguration with dependency (id *3 and snapshot)",
            "b2"
        )
        .addBCCase(
            "find buildConfiguration with dependency (dependency(id *3 and snapshot) and artifact)",
            "b4"
        )
        .addTempCase(
            "find template with dependency (id *4 and snapshot)",
            "t1"
        )
        .addTempCase(
            "find template with dependency (id *4 and artifact)",
            "t2"
        )
        .addBCCase(
            "find buildConfiguration with dependency (id *4 and (snapshot or artifact))",
            "b6"
        )
        .addBCCase(
            "find buildConfiguration with dependency[all] (id *4 and (snapshot or artifact))",
            "b5", "b6"
        )
        .addTempCase(
            "find template with dependency (snapshot option opt1=abc)",
            "t3"
        )
        .addBCCase(
            "find buildConfiguration with dependency[all] (snapshot option opt1=bcd)",
            "b5"
        )
        .addTempCase(
            "find template with dependency (artifact rules *bac*)",
            "t2"
        )
        .addBCCase(
            "find buildConfiguration with dependency (artifact (rules *tii*) and dependency (artifact rules *wer*))",
            "b6"
        )
        .end()

    @DataProvider(name = "compl")
    fun complData() = TestDataProvider()
        .addComplCase(
            "find template with dependency snapshot option o",
            "opt1"
        )
        .addComplCase(
            "find template with dep",
            "dependency"
        )
        .addComplCase(
            "find template with dependency sn",
            "snapshot"
        )
        .addComplCase(
            "find template with dependency ar",
            "artifact"
        )
        .addComplCase(
            "find template with dependency snapshot o",
            "option"
        )
        .addComplCase(
            "find template with dependency artifact r",
            "rules"
        )
        .addComplCase(
            "find template with dependency artifact ",
            "rules"
        )
        .addComplCase(
            "find buildConfiguration with dependency artifact rules zxc",
            "zxcvzxcvzc"
        )
        .addComplCase(
            "find template with dependency artifact rules abac",
            "abacabadaba"
        )
        .end()

    @DataProvider(name = "failed")
    fun failedData() = TestFailedDataProvdier()
        .addParseCase("find buildConfiguration with dependency type vcs")
        .addParseCase("find project with dependency id Base")
        .addParseCase("find vcsRoot with dependency")
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
        sleep(50)
        checkVars(query, expected)
    }
}