package duplicateres.plugin

import org.gradle.api.provider.SetProperty

interface DuplicateResourceExtension {

    val excludeResourceType: SetProperty<ResourceType>
}