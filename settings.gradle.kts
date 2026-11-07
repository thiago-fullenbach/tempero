pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
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

rootProject.name = "Tempero"
include(":app")
include(":core:model")
include(":core:domain")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:analytics")
include(":core:designsystem")
include(":core:ui")
include(":feature:explore")
include(":feature:search")
include(":feature:favorites")
include(":feature:detail")
include(":core:testing")
 