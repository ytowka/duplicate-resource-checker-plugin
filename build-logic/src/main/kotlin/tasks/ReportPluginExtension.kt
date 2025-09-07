package tasks

import org.gradle.api.file.DirectoryProperty

interface ReportPluginExtension {
    val sourceDirectory: DirectoryProperty
}