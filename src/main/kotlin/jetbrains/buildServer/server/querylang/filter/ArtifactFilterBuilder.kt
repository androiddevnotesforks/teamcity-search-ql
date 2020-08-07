package jetbrains.buildServer.server.querylang.filter

import jetbrains.buildServer.server.querylang.ast.ArtifactDepFilterType
import jetbrains.buildServer.server.querylang.ast.RulesFilter
import jetbrains.buildServer.server.querylang.parser.StringConditionVisitor

object ArtifactFilterBuilder : FilterBuilder<ArtifactDepFilterType, DependencyFilterBuilder.MyArtifactDependency> {
    override fun createFilter(
        filter: ArtifactDepFilterType,
        context: Any?
    ): ObjectFilter<DependencyFilterBuilder.MyArtifactDependency> {
        return when(filter) {
            is RulesFilter -> {
                val conditionFilter = StringFilterBuilder.createFilter(filter.strCondition)
                ObjectFilter {obj ->
                    conditionFilter.accepts(obj.dep.sourcePaths)
                }
            }
            else -> throw IllegalStateException("Unknown filter '${filter::class.java}' of ArtifactDependencyFilterType")
        }
    }
}