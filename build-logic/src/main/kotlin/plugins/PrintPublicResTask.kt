package plugins

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class PrintPublicResTask : DefaultTask(){


    @get:Input
    abstract val resFiles: Property<FileCollection>

    @get:Input
    abstract val localResFiles: Property<FileCollection>

    @TaskAction
    fun act() {

        resFiles.get()
            .filter { it.path.contains(BUILD_DIRECTORY) }
            .forEach {
                it.bufferedReader().forEachLine {
                    println(it)
                }
                println()
            }


        localResFiles.get()
            .filter { it.path.contains(BUILD_DIRECTORY) }
            .forEach {
                it.bufferedReader().forEachLine {
                    println(it)
                }
                println()
            }
    }

    companion object {

        const val BUILD_DIRECTORY = "/build/"
    }
}