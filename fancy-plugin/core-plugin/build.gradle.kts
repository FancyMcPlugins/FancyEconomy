group = "de.oliver"
version = rootProject.version

plugins {
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    compileOnly(project(":fancy-plugin:core-folia"))

    compileOnly(fileTree("../../lib") {
        include("*.jar")
    })
}

tasks {
    processResources {
        filesMatching(listOf("**plugin.yml")) {
            expand(
                    "projectVersion" to project.version,
                    "projectDescription" to project.description
            )
        }
    }
}