plugins {
    java
    kotlin("jvm")
    kotlin("kapt")
}

group = "${ModuleConfig.GroupNameBase}.integration.discord"

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":integration:discord"))
    implementation(Dependencies.Libraries.Jda.Jda)
    implementation(Dependencies.Libraries.Slf4j.Slf4jApi)
    implementation(Dependencies.Libraries.RxJava3.RxJava)
    implementation(Dependencies.Libraries.RxJava3.RxKotlin)
    implementation(Dependencies.Libraries.AutoService.Annotations)
    kapt(Dependencies.Libraries.AutoService.Compiler)
}
