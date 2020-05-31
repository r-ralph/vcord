plugins {
    application
    kotlin("jvm")
}

group = "${ModuleConfig.GroupNameBase}.launcher"

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(project(":launcher"))
    implementation(project(":integration:discord:mock"))
    implementation(Dependencies.Libraries.Logback.LogbackClassic)
}

tasks.withType(Jar::class.java) {
    manifest {
        attributes["Main-Class"] = "ms.ralph.vcord.VcordMain"
    }
}

application {
    @Suppress("UnstableApiUsage")
    mainClass.set("ms.ralph.vcord.VcordMain")
}
