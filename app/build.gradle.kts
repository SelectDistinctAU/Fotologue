plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.fotologue.app"
    compileSdk = 37
    ndkVersion = "30.0.16138531"

    defaultConfig {
        applicationId = "com.fotologue.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 15
        versionName = "1.0.14"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        val buildTime = System.currentTimeMillis()
        buildConfigField("Long", "BUILD_TIME", "${buildTime}L")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.compose.adaptive)
    implementation(libs.androidx.compose.adaptive)
    implementation(libs.androidx.compose.adaptive.layout)
    implementation(libs.androidx.compose.adaptive.navigation3)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logging.interceptor)
    implementation(libs.material)
    implementation(libs.okhttp)
    implementation(libs.play.services.location)
    implementation(libs.mlkit.face.detection)
    testImplementation(libs.androidx.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.mockk.android)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.hilt.android)
    "ksp"(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    "ksp"(libs.androidx.room.compiler)
}
