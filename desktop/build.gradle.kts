import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1")
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite.jdbc)
}

compose.desktop {
    application {
        mainClass = "com.fotologue.desktop.MainKt"
        jvmArgs("--enable-native-access=ALL-UNNAMED")
        nativeDistributions {
            targetFormats(
                TargetFormat.Exe,
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            packageName = "FotologueDesktop"
            packageVersion = "1.0.0"
            description = "Fotologue Analogue & Hybrid Photography Desktop Command Center"
            copyright = "© 2026 Fotologue. All rights reserved."
        }
    }
}

tasks.register<JavaExec>("runMaintainer") {
    group = "application"
    description = "Run the Fotologue Master Preset Authoring Tool"
    mainClass.set("com.fotologue.desktop.MaintainerAppKt")
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
