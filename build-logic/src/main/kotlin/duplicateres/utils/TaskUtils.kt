package duplicateres.utils

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import java.io.File

fun DefaultTask.findProjectByResFile(file: File): Project{
    val projectPath = file.path.substringBefore("/build/")
    return project.rootProject.allprojects.find { it.projectDir.path == projectPath } ?: throw Exception("project with path ${file.path} not found")
}