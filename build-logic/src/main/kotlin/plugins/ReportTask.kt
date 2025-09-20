package plugins

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class ReportTask : DefaultTask() {

    /*@get:InputFile
    abstract val res: RegularFileProperty*/

    @get:InputFiles
    abstract val inputFiles: ListProperty<Directory>

    @TaskAction
    fun act() {
        println("got ${inputFiles.get().size} files")
        inputFiles.get().forEach {
            println("file://"+it.asFile.path)
        }
       /* val file = res.asFile.get()
        val content = file.bufferedReader()
            .forEachLine {
                println(it)
            }*/
    }
}
