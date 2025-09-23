package com.alfabank.duplicateres.utils

import com.android.build.api.variant.Variant
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.provider.Provider
import org.gradle.internal.extensions.stdlib.capitalized
import java.io.File

private const val BASELINE_DIRECTORY = "duplicateResBaseline"
private const val BASELINE_FILE_EXTENSION = ".txt"

internal fun Project.configurePlugin(
    name: String,
    action: Action<AppliedPlugin>,
) = pluginManager.withPlugin(name) {
    action.execute(this)
}

internal fun Project.findParseLocalResourceTask(variant: Variant): Provider<Task> {
    return tasks.named("parse${variant.name.capitalized()}LocalResources")
}

internal fun Project.getBaselineFileProvider(variant: Variant): Provider<RegularFile> {
    val baselineFile = file("$BASELINE_DIRECTORY/${variant.name}$BASELINE_FILE_EXTENSION")
        .takeIf { it.exists() }
    val property = objects.fileProperty()
    property.set(baselineFile)
    return property
}

internal fun Project.getBaselineFile(variant: Variant): File {
    return file("$BASELINE_DIRECTORY/${variant.name}$BASELINE_FILE_EXTENSION")
}

internal fun Project.buildProjectsMap(): Map<String, String> {
    return rootProject.allprojects.associate {
        it.projectDir.path to it.path
    }
}
