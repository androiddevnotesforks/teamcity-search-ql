package jetbrains.buildServer.server.querylang.ui

import jetbrains.buildServer.controllers.FormUtil
import jetbrains.buildServer.controllers.admin.AdminOverviewBean
import jetbrains.buildServer.controllers.admin.AdminPage
import jetbrains.buildServer.controllers.admin.ProjectAdminForm
import jetbrains.buildServer.server.querylang.parser.QueryParser
import jetbrains.buildServer.server.querylang.requests.ConsoleResultPrinter
import jetbrains.buildServer.server.querylang.requests.RequestClient
import jetbrains.buildServer.server.querylang.requests.InternalApiQueryHandler

import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.web.openapi.Groupable
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.util.CameFromSupport
import javax.servlet.http.HttpServletRequest

class SearchAdminPage(
    private val pluginDescriptor: PluginDescriptor,
    pagePlaces: PagePlaces,
    projectManager_: ProjectManager
) : AdminPage(
        pagePlaces,
        "search",
        pluginDescriptor.getPluginResourcesPath("example.jsp"),
        "Search"
    )
{

    private val projectManager: ProjectManager = projectManager_
    private val requestClient: RequestClient
    val parser = QueryParser()
    init {
        requestClient = RequestClient(InternalApiQueryHandler(projectManager), ConsoleResultPrinter)
        register()
    }

    override fun fillModel(model: MutableMap<String, Any>, request: HttpServletRequest) {
        val form = FormUtil.getOrCreateForm(
            request,
            ProjectAdminForm::class.java
        ) { ProjectAdminForm() }!!
        FormUtil.bindFromRequest(request, form)

        val bean = AdminOverviewBean(form, projectManager)
        model["adminOverviewForm"] = bean
        model["searchResult"] = bean.keyword?.let {requestClient.process(parser.parse(it))} ?: ""
        CameFromSupport.setupCameFromUrl(model, request)
    }

    override fun getGroup(): String {
        return Groupable.PROJECT_RELATED_GROUP
    }

}