import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "com.semivanilla.squaremapplayers"
version = "1.0.0-SNAPSHOT"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT") // Paper
    compileOnly("xyz.jpenilla:squaremap-api:1.1.0-SNAPSHOT") // Squaremap
    shadow("org.spongepowered:configurate-yaml:4.1.2") // Configurate
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    compileOnly("com.semivanilla:Bounties:1.9")
}

tasks {
    runServer {
        minecraftVersion("1.18.2")
    }

    shadowJar {
        dependsOn(getByName("relocateJars") as ConfigureShadowRelocation)
        archiveFileName.set("${project.name}-${project.version}.jar")
        minimize()
        configurations = listOf(project.configurations.shadow.get())
    }

    build {
        dependsOn(shadowJar)
    }

    create<ConfigureShadowRelocation>("relocateJars") {
        target = shadowJar.get()
        prefix = "${project.name}.lib"
    }
}

bukkit {
    name = rootProject.name
    main = "$group.${rootProject.name}"
    version = "${rootProject.version}-${gitCommit()}"
    apiVersion = "1.18"
    website = "https://github.com/SemiVanilla-MC/${rootProject.name}"
    authors = listOf("SemiVanilla-MC")
    depend = listOf("squaremap")
    softDepend = listOf("Bounties")
}

fun gitCommit(): String {
    val os = ByteArrayOutputStream()
    project.exec {
        commandLine = "git rev-parse --short HEAD".split(" ")
        standardOutput = os
    }
    return String(os.toByteArray()).trim()
}