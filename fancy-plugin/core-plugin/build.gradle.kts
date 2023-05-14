group = "de.oliver"
version = rootProject.version

plugins {
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

dependencies {
    paperweight.foliaDevBundle("1.19.4-R0.1-SNAPSHOT")

    implementation("dev.jorel:commandapi-bukkit-shade:9.0.1")
    compileOnly("dev.jorel:commandapi-annotations:9.0.1")
    annotationProcessor("dev.jorel:commandapi-annotations:9.0.1")

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