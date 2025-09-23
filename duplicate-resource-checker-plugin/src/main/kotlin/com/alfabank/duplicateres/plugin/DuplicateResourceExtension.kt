package com.alfabank.duplicateres.plugin

import org.gradle.api.provider.SetProperty

public interface DuplicateResourceExtension {

    public val excludeResourceType: SetProperty<ResourceType>
}
