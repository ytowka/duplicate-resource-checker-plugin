package ru.alfabank.android.duplicateres.utils

import com.android.build.api.variant.Variant
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.artifacts.DefaultProjectComponentIdentifier
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.provider.Provider
import org.gradle.internal.extensions.stdlib.capitalized
import ru.alfabank.android.duplicateres.internal.ArtifactMetadata
import java.io.File

private const val BASELINE_FILE_EXTENSION = ".txt"

internal fun Project.configurePlugin(
    name: String,
    action: Action<AppliedPlugin>,
) = pluginManager.withPlugin(name) {
    action.execute(this)
}

internal fun ArtifactCollection.mapToMetadata(): Provider<List<ArtifactMetadata>> {
    return resolvedArtifacts.map { resolvedArtifacts ->
        resolvedArtifacts.map { artifact ->
            ArtifactMetadata(
                filePath = artifact.file.path,
                isProject = artifact.variant.owner is DefaultProjectComponentIdentifier,
                displayName = artifact.variant.owner.displayName,
            )
        }
    }
}

internal fun Project.findParseLocalResourceTask(variant: Variant): Provider<Task> {
    return tasks.named("parse${variant.name.capitalized()}LocalResources")
}

internal fun Project.getBaselineFileProvider(variant: Variant, baselinePath: String): Provider<RegularFile> {
    val baselineFile = file("$baselinePath/${variant.name}$BASELINE_FILE_EXTENSION")
        .takeIf { it.exists() }
    val property = objects.fileProperty()
    property.set(baselineFile)
    return property
}

internal fun Project.getBaselineFile(variant: Variant, baselinePath: String): File {
    return file("$baselinePath/${variant.name}$BASELINE_FILE_EXTENSION")
}
