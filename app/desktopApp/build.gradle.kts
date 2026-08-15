import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    // Shared app code
    implementation(projects.app.shared)

    // Desktop runtime
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    // Tooling
    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "dev.elelan.quotequiz.MainKt"
        jvmArgs("-Dapple.awt.application.name=Wisdom Trivia")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Wisdom Trivia"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "dev.elelan.quotequiz"
                iconFile.set(project.file("src/main/resources/AppIcon.icns"))

                infoPlist {
                    extraKeysRawXml = """
                        <key>CFBundleDisplayName</key>
                        <string>Wisdom Trivia</string>
                        <key>CFBundleName</key>
                        <string>Wisdom Trivia</string>
                    """.trimIndent()
                }
            }
        }
    }
}