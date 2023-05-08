plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "de.oliver"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}