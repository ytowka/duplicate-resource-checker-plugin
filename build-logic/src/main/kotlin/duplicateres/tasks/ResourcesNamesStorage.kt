package duplicateres.tasks

import java.io.File

class ResourcesNamesStorage {

    private val storage = mutableMapOf<String, MutableMap<String, MutableList<File>>>()

    fun add(
        type: String,
        name: String,
        rFile: File
    ) {
        if (!storage.contains(type)) {
            storage[type] = mutableMapOf()
        }
        if (!storage[type]!!.contains(name)) {
            storage[type]!![name] = mutableListOf(rFile)
        } else {
            storage[type]!![name]!!.add(rFile)
        }
    }

    fun forEach(action: (Map.Entry<String, Map<String, List<File>>>) -> Unit) {
        storage.forEach(action)
    }
}
