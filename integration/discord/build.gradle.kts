plugins {
    java
    kotlin("jvm")
}

group = "${ModuleConfig.GroupNameBase}.integration"

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(project(":core:manager"))
    api(project(":integration:audio"))
}
