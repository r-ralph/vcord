plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin") version Dependencies.Versions.JavaFxPlugin
}

group = ModuleConfig.GroupNameBase

configure<JavaPluginConvention> {
    sourceCompatibility = ModuleConfig.sourceCompatibility
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core:model"))
    implementation(project(":ui:controller"))
    implementation(Dependencies.Libraries.Slf4j.Slf4jApi)
    implementation(Dependencies.Libraries.RxJava3.RxJava)
    implementation(Dependencies.Libraries.RxJava3.RxKotlin)
}

javafx {
    version = "13"
    modules = listOf("javafx.controls", "javafx.fxml")
}
