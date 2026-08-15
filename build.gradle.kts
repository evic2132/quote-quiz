import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.gradleDotenv) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSpring) apply false
    alias(libs.plugins.kotlinJpa) apply false
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.springDependencyManagement) apply false
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
}

detekt {
    parallel = true
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = false
    ignoreFailures = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    baseline = rootProject.file("config/detekt/baseline.xml")
    source.setFrom(
        files(
            "api-contract/src",
            "app/androidApp/src",
            "app/desktopApp/src",
            "app/shared/src",
            "server/src",
        ),
    )
    basePath.set(rootProject.projectDir)
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        sarif.required.set(true)
    }
}
