package duplicateres.tasks


import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

abstract class CreateBaselineDuplicateResourcesTask : BaseDuplicateResourcesTask(){

    @get:Input
    abstract val baselineFilePath: Property<String>

    @get:OutputFile
    abstract val baselineFile: RegularFileProperty

    @TaskAction
    fun act() {
        val resourcesNamesStorage = ResourcesNamesStorage()
        populateResourceStorage(resourcesNamesStorage)

        val duplicates = findResourceDuplicates(resourcesNamesStorage, ResourcesNamesStorage())
        //baselineFile.set(createBaseline(duplicates))
    }

    private fun createBaseline(duplicates: ResourcesNamesStorage): File {
        val file = project.file(baselineFilePath)
        file.createNewFile()
        PrintWriter(file).use { writer ->
            duplicates.forEach { (type, resourcesInProjects) ->
                resourcesInProjects.forEach { (resName, projects) ->
                    writer.println(buildString {
                        append(type)
                        append(" ")
                        append(resName)
                        append(projects.joinToString(separator = " "))
                    })
                }
            }
        }
        return file
    }
}