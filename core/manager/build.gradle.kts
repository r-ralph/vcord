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
    api(project(":core:model"))
    implementation(Dependencies.Libraries.Slf4j.Slf4jApi)
    implementation(Dependencies.Libraries.RxJava3.RxJava)
    implementation(Dependencies.Libraries.RxJava3.RxKotlin)
}
