import io.izzel.taboolib.gradle.*

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.11"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

taboolib {
    env {
        install(UNIVERSAL, BUKKIT, BUKKIT_ALL)
        install(
            CHAT,
            UI,
            CONFIGURATION,
            EXPANSION_COMMAND_HELPER
        )

        repoTabooLib = project.repositories.mavenLocal().url.toString()
        version {
            taboolib = "6.1.0-local-dev"
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("ink.ptms.core:v12004:12004-minimize:mapped")
    compileOnly("ink.ptms.core:v12004:12004-minimize:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}