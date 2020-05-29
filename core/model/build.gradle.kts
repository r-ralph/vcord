plugins {
    java
    kotlin("jvm")
}

group = "${ModuleConfig.GroupNameBase}.core"

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
