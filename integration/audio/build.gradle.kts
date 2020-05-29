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
    implementation(project(":core:manager"))
    implementation(Dependencies.Libraries.Slf4j.Slf4jApi)
    implementation(Dependencies.Libraries.RxJava3.RxJava)
    implementation(Dependencies.Libraries.RxJava3.RxKotlin)
}
