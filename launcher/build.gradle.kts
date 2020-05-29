plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = ModuleConfig.GroupNameBase

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core:manager"))
    implementation(project(":ui:impl"))
    implementation(project(":integration:audio"))
    implementation(project(":integration:discord"))
    implementation(project(":integration:midi"))
    implementation(Dependencies.Libraries.KotlinX.Serialization)
    implementation(Dependencies.Libraries.Kaml.Kaml)
    implementation(Dependencies.Libraries.Slf4j.Slf4jApi)
}
