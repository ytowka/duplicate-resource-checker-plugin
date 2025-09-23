package com.alfabank.duplicateres.internal

internal class ResourcesNamesStorage {

    private val storage = mutableMapOf<String, MutableMap<String, MutableList<String>>>()

    fun add(
        type: String,
        name: String,
        project: String
    ) {
        val resources = storage.getOrPut(type) { mutableMapOf() }
        val projectList = resources.getOrPut(name) { mutableListOf() }
        projectList.add(project)
    }

    fun addAll(
        type: String,
        name: String,
        projects: List<String>
    ) {
        val resources = storage.getOrPut(type) { mutableMapOf() }
        val projectList = resources.getOrPut(name) { mutableListOf() }
        projectList.addAll(projects)
    }

    fun getProjects(type: String, name: String): List<String> {
        return storage[type]?.get(name) ?: emptyList()
    }

    val isEmpty: Boolean
        get() = storage.isEmpty()

    fun forEach(action: (Map.Entry<String, Map<String, List<String>>>) -> Unit) {
        storage.forEach(action)
    }
}
