import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Dependencies.Versions.Kotlin apply false
    kotlin("kapt") version Dependencies.Versions.Kotlin apply false
    kotlin("plugin.serialization") version Dependencies.Versions.Kotlin apply false
    id("ms.ralph.gradle.dependency-plantuml-exporter") version Dependencies.Versions.DependencyPlantUmlExporter
}

allprojects {
    version = "1.0-SNAPSHOT"
}

subprojects {
    repositories {
        mavenCentral()
        jcenter()
    }

    tasks.withType(KotlinCompile::class.java) {
        kotlinOptions {
            jvmTarget = "13"
        }
    }
}

dependencyPlantUmlExporter {
    packageIdProvider = { project ->
        when (project.path) {
            ":launcher" -> ":launcher"
            ":integration:discord" -> ":integration:discord"
            ":integration:audio" -> ":integration:audio"
            ":integration:midi" -> ":integration:midi"
            else -> project.parent?.path ?: ""
        }
    }
}
