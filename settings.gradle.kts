pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

includeBuild("build-logic")
includeBuild("mylibrary")
includeBuild("duplicate-resource-checker-plugin")

rootProject.name = "DuplicateResourceCheckerPlugin"
include(":app")
include(":feature-a")
include(":feature-b")
include(":feature-c")
include(":feature-c:subfeature-c")
