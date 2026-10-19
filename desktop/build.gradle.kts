import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvmToolchain(21)
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
        javaHome = "C:/Users/geewy/.jdks/jbr-21.0.11"
        nativeDistributions {
            includeAllModules = true
            targetFormats(
                TargetFormat.Exe,
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            packageName = "FotologueDesktop"
            packageVersion = "1.0.2"
            description = "Fotologue Analogue & Hybrid Photography Desktop Command Center"
            copyright = "© 2026 Fotologue. All rights reserved."
            windows {
                upgradeUuid = "a2d1847e-8c3b-4b10-a7d1-9a101038dd80"
                menu = true
                shortcut = true
            }
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

tasks.register("packageWindows") {
    group = "distribution"
    description = "Packages both Windows MSI and EXE native installers for Fotologue Desktop"
    dependsOn("packageMsi", "packageExe")
}
