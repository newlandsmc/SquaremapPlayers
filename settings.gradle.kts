rootProject.name = "SquaremapPlayers"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
//        mavenLocal()
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
        maven { // Configurate
            url = uri("https://repo.spongepowered.org/maven")
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}


