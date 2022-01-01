rootProject.name = "SquaremapPlayers"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { // Paper
            url = uri("https://papermc.io/repo/repository/maven-public/")
        }
        maven { // run paper plugin
            url = uri("https://repo.jpenilla.xyz/snapshots/")
            mavenContent {
                snapshotsOnly()
                includeGroup("xyz.jpenilla")
            }
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}


