plugins {
    java
    kotlin("jvm")
}

group = "${ModuleConfig.GroupNameBase}.ui"

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
