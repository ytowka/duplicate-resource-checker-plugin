package com.alfabank.duplicateres.internal

internal fun ResourcesNamesStorage.addName(
    resourceLine: String,
    excludes: Set<String>,
    project: String
) {
    val (type, resourceName) = resourceLine.split(" ")
    if (type !in excludes) {
        add(type, resourceName, project)
    }
}

internal fun ResourcesNamesStorage.findDuplicates(
    baseline: ResourcesNamesStorage
): ResourcesNamesStorage {
    val result = ResourcesNamesStorage()
    forEach { (type, resourcesInProjects) ->
        resourcesInProjects.forEach { (resName, projects) ->
            checkResourceName(projects, baseline, type, resName, result)
        }
    }
    return result
}

internal fun checkResourceName(
    projects: List<String>,
    baseline: ResourcesNamesStorage,
    type: String,
    resName: String,
    result: ResourcesNamesStorage
) {
    if (projects.size > 1) {
        val baselineProjects = baseline.getProjects(type, resName)
        if (projects.sorted() != baselineProjects.sorted()) {
            result.addAll(type, resName, projects)
        }
    }
}
