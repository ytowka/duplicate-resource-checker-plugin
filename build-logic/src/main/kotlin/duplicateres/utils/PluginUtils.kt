package duplicateres.utils

import com.android.build.api.variant.Variant
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.internal.extensions.stdlib.capitalized
import java.io.File

private const val BASELINE_DIRECTORY = "duplicateResBaseline"
private const val BASELINE_FILE_EXTENSION = ".txt"

fun Project.findParseLocalResourceTask(variant: Variant): Provider<Task> {
    return tasks.named("parse${variant.name.capitalized()}LocalResources")
}

fun Project.getBaselineFileProvider(variant: Variant): Provider<RegularFile> {
    val baselineFile = file("$BASELINE_DIRECTORY/${variant.name}$BASELINE_FILE_EXTENSION")
        .takeIf { it.exists() }
    val property = objects.fileProperty()
    property.set(baselineFile)
    return property
}

fun Project.getBaselineFile(variant: Variant): File {
    return file("$BASELINE_DIRECTORY/${variant.name}$BASELINE_FILE_EXTENSION")
}