package duplicateres.tasks


class ResourcesNamesStorage {

    private val storage = mutableMapOf<String, MutableMap<String, MutableList<String>>>()

    fun add(
        type: String,
        name: String,
        project: String
    ) {
        if (!storage.contains(type)) {
            storage[type] = mutableMapOf()
        }
        if (!storage[type]!!.contains(name)) {
            storage[type]!![name] = mutableListOf(project)
        } else {
            storage[type]!![name]!!.add(project)
        }
    }

    fun addAll(
        type: String,
        name: String,
        projects: List<String>
    ) {
        if (!storage.contains(type)) {
            storage[type] = mutableMapOf()
        }
        if (!storage[type]!!.contains(name)) {
            storage[type]!![name] = projects.toMutableList()
        } else {
            storage[type]!![name]!!.addAll(projects)
        }
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
