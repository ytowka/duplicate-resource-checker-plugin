package duplicateres.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class FindDuplicateResourcesTask : DefaultTask(){

    @get:Input
    abstract val libraryResFiles: Property<FileCollection>

    @get:Input
    abstract val localResFiles: Property<FileCollection>

    @get:Input
    abstract val excludeResTypes: ListProperty<String>

    @get:InputFile
    @get:Optional
    abstract val baselineFile: RegularFileProperty

    @TaskAction
    fun act() {
        //println(baselineFile.isPresent)
    }
}