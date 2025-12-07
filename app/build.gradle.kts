import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import shadow.bundletool.com.android.tools.r8.internal.li

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room.gradle.plugin)
}

// use Room gradle plugin to setup the schema directory
// https://developer.android.com/training/data-storage/room/migrating-db-versions#kotlin_3
room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.example.paging"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.paging"
        minSdk = 31
        targetSdk = 36
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
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // constraint layout
    implementation(libs.androidx.contraintlayout.compose)

    // navigation 3 related
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
    // paging 3 related
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)
    testImplementation(libs.androidx.paging.testing)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    testImplementation(libs.androidx.room.testing)
    // Koin
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.koin.android)
    ksp(libs.koin.compiler)
    implementation(libs.koin.androidx.compose)
    testImplementation(libs.koin.android.test)

    // support viewModel() injection like `conversationViewModel: ConversationViewModel = viewModel()`
    implementation(libs.androidx.viewmodel.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ktor
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation) // Ktor JSON request/response support
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.resources) // Type-safe requests, https://ktor.io/docs/client-resources.html
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}