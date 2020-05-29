plugins {
    java
    kotlin("jvm")
    kotlin("kapt")
}

group = "${ModuleConfig.GroupNameBase}.ui"

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core:manager"))
    api(project(":ui:model"))
    implementation(Dependencies.Libraries.RxJava3.RxJava)
    implementation(Dependencies.Libraries.RxJava3.RxKotlin)
    implementation(Dependencies.Libraries.AutoService.Annotations)
    kapt(Dependencies.Libraries.AutoService.Compiler)
}
