package duplicateres.plugin

import org.gradle.api.provider.ListProperty


interface DuplicateResourceExtension {

    val excludeResourceType: ListProperty<String>
}