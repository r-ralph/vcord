plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version Dependencies.Versions.ShadowPlugin
}

group = "${ModuleConfig.GroupNameBase}.launcher"

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(project(":launcher"))
    implementation(project(":integration:discord:real"))
    implementation(Dependencies.Libraries.Logback.LogbackClassic)
}

tasks.withType(Jar::class.java) {
    manifest {
        attributes["Main-Class"] = "ms.ralph.vcord.VcordMain"
    }
}

application {
    mainClassName = "ms.ralph.vcord.VcordMain"
}
