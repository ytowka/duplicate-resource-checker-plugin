package plugins

import org.gradle.api.Project
import org.gradle.api.artifacts.component.ProjectComponentSelector
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult

private class ProjectTree(root: String) : Collection<String> {

    private val rootNode: ProjectNode = ProjectNode(path = root)

    private val nodes: MutableMap<String, ProjectNode> = mutableMapOf(root to this.rootNode)

    fun insert(dependent: String, path: String) {
        if (dependent == path) return
        val dependentNode = nodes[dependent] ?: error("Node not found")

        nodes.getOrPut(path) { ProjectNode(path = path) }
            .dependents += dependentNode
    }

    fun dependents(path: String): Set<String> {
        fun dependents(node: ProjectNode): List<String> {
            return buildList {
                node.dependents.forEach {
                    add(it.path)
                    addAll(dependents(it))
                }
            }
        }

        val requestedNode = nodes[path] ?: error("Node not found")
        return buildSet {
            add(requestedNode.path)
            addAll(dependents(requestedNode))
        }
    }

    override val size: Int get() = nodes.size

    override fun contains(element: String): Boolean = nodes.keys.contains(element)

    override fun containsAll(elements: Collection<String>): Boolean = nodes.keys.containsAll(elements)

    override fun isEmpty(): Boolean = nodes.isEmpty()

    override fun iterator(): Iterator<String> = nodes.keys.iterator()

    private data class ProjectNode(
        val path: String,
        val dependents: MutableSet<ProjectNode> = mutableSetOf(),
    )
}

private fun ProjectTree.fill(
    parent: String,
    component: ResolvedComponentResult,
    resolvedDependencies: MutableSet<String>,
    root: Project,
) {
    val dependencies = component.dependencies.filter { it.requested is ProjectComponentSelector }
    dependencies.forEach { dep ->
        val project = dep.requested as ProjectComponentSelector
        val projectPath = project.projectPath
        insert(parent, projectPath)

        root.findProject(projectPath)

        if (dep !is ResolvedDependencyResult) return@forEach
        if (resolvedDependencies.contains(projectPath)) return@forEach
        resolvedDependencies += projectPath
        dep.requested
        fill(projectPath, dep.selected, resolvedDependencies, root)
    }
}

internal fun Project.getProjectDependencies(
    configuration: String,
): Set<String> {
    val projectTree = ProjectTree(root = project.path)
    val rootComponents = project.configurations.matching { it.name == configuration }
        .map { it.incoming.resolutionResult.rootComponent }


    // Long operation (contains side effects)
    for (component in rootComponents) {
        val resolvedDependencies: MutableSet<String> = mutableSetOf()
        projectTree.fill(project.path, component.get(), resolvedDependencies, rootProject)
        for (projectPath in resolvedDependencies) {
            if (projectPath != project.path) project.evaluationDependsOn(projectPath)
        }
    }

    return projectTree.flatMap { projectPath ->
        projectTree.dependents(projectPath)
    }.toSet()
}
