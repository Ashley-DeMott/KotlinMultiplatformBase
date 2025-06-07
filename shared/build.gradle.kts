import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

// Kotlin Multiplatform
kotlin {
    // Android targets
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }


    // iOS targets
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        // Dependencies for just Android go here
        androidMain.dependencies {
            // Android Activity using Compose
            implementation(libs.androidx.activity.compose)
        }

        // Multiplatform dependencies go here
        commonMain.dependencies {
            // Kotlin standard library
            implementation(libs.kotlin.stdlib.common)

            // Compose UI libraries
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            // Serialization for Json -> Object translation
            implementation(libs.kotlinx.serialization.json)
            // TODO [GRADLE]: Add proper gson implementation
            /*
            implementation(libs.converter.gson)
            implementation(libs.gson)
            implementation(libs.converter.gson.v210) // Using a specific version
             */

            // TODO [GRADLE]: Add Retrofit
            /*
            implementation(libs.retrofit2.kotlinx.serialization.converter)
            implementation(libs.retrofit)
            implementation(libs.okhttp)
            */

            // DateTime, way to store and calculate time measurements
            implementation(libs.kotlinx.datetime)

            // Kermit logging library
            implementation(libs.kermit)

            // Previewing for Compose, seems to only work with Android and not common
            //debugImplementation(libs.compose.ui.tooling)
            //implementation(libs.compose.ui.tooling.preview)
        }

        // Dependencies for just testing the common code goes here
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// Android specific data
android {
    namespace = "com.galaticashgames.${rootProject.name}"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.androidx.runtime.android)
}
