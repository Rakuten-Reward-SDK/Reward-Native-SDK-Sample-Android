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
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://raw.github.com/rakuten-ads/Rakuten-Reward-Native-Android/master/maven")
        maven(url = "https://artifactory.rakuten-it.com/membership-mvn-release/")
    }
}

rootProject.name = "Reward SDK Sample"
include(":app")
 