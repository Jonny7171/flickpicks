plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.flickpicks"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.flickpicks"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    // ViewModel, LiveData, and Compose Navigation
    implementation(libs.androidx.lifecycle.viewmodel.compose.v286)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose.v282)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.junit.ktx)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)

    // Coroutine support
    implementation(libs.kotlinx.coroutines.test)

    // Compose testing (for UI tests)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Unit testing dependencies (JUnit 5, MockK, Robolectric)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.mockk.v1132)
    testImplementation(libs.kotlinx.coroutines.test.v173)
    testImplementation(libs.androidx.lifecycle.livedata.ktx)
    testImplementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.androidx.core.testing.v210)
    testImplementation(libs.robolectric)

    // Test Task Configuration for JUnit 5
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}