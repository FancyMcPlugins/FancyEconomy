plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "2.1.0" // Adds runServer and runMojangMappedServer tasks for testing
//    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.oliver"
version = findProperty("version")!!
description = findProperty("description").toString()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
//    paperweight.foliaDevBundle("1.19.4-R0.1-SNAPSHOT")
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")

    implementation("com.github.FancyMcPlugins:FancyLib:f2a7b13071")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    compileOnly("me.clip:placeholderapi:2.11.3")

    implementation("dev.jorel:commandapi-bukkit-shade:9.0.1")
    compileOnly("dev.jorel:commandapi-annotations:9.0.1")
    annotationProcessor("dev.jorel:commandapi-annotations:9.0.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    runServer{
        minecraftVersion("1.19.4")
    }

    shadowJar{
        archiveClassifier.set("")
        relocate("dev.jorel.commandapi", "de.oliver.fancycoins.commandapi")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(project.components["java"])
            }
        }
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}