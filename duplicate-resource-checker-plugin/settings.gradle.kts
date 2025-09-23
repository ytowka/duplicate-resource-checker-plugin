rootProject.name = "duplicate-resource-checker-plugin"

pluginManagement {
    repositories {
        maven(url = "https://binary.alfabank.ru/artifactory/public")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://binary.alfabank.ru/artifactory/public")
    }
}
