package ru.alfabank.android.duplicateres.plugin

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

public interface DuplicateResourceExtension {

    public val baselinePath: Property<String>
    public val excludeResourceType: SetProperty<ResourceType>
}
