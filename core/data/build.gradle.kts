plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "com.fotologue.core.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("test").assets.directories.add("$projectDir/schemas")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hilt.android)

    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Unit & Database Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    testImplementation("org.robolectric:robolectric:4.14.1")
}

tasks.register("assembleMasterCatalog") {
    group = "fotologue"
    description = "Compiles all modular Maintainer JSON files into the final master_preset_catalog.json for release"
    
    val presetsDir = file("src/main/resources/presets")
    val assetsDir = file("src/main/assets")
    val outputFile = file("src/main/assets/master_preset_catalog.json")

    inputs.dir(presetsDir)
    outputs.file(outputFile)

    doLast {
        if (!assetsDir.exists()) assetsDir.mkdirs()
        if (!presetsDir.exists() || presetsDir.listFiles()?.isEmpty() == true) {
            println("No modular preset files found in src/main/resources/presets/")
            outputFile.writeText("[]")
            return@doLast
        }

        val jsonFiles = presetsDir.listFiles { _, name -> name.endsWith(".json") } ?: emptyArray()
        val allJsonObjects = mutableListOf<String>()

        jsonFiles.forEach { file ->
            val text = file.readText().trim()
            if (text.startsWith("[") && text.endsWith("]")) {
                val content = text.substring(1, text.length - 1).trim()
                if (content.isNotEmpty()) {
                    allJsonObjects.add(content)
                }
            } else {
                throw GradleException("Invalid JSON format in ${file.name}. Expected a JSON array.")
            }
        }

        val combinedJson = "[\n${allJsonObjects.joinToString(",\n")}\n]"
        outputFile.writeText(combinedJson)
        println("✅ Successfully assembled ${jsonFiles.size} modular files into master_preset_catalog.json")
    }
}

tasks.named("preBuild").configure {
    dependsOn("assembleMasterCatalog")
}
