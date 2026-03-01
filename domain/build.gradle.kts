plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    kotlin("plugin.serialization") version "1.9.0" // or your Kotlin version
    id("com.squareup.sqldelight") version "1.5.5"
}

sqldelight {
    database("SurveyDatabase") {
        packageName = "surveydb"   // this becomes the Kotlin package
        sourceFolders = listOf("sqldelight")
    }
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "io.pula.data"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "dataKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("com.squareup.sqldelight:runtime:1.5.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1") // or latest
                implementation("io.ktor:ktor-client-core:2.3.2")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }

        val androidHostTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.work:work-runtime-ktx:2.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("com.squareup.sqldelight:android-driver:1.5.5")
                implementation("io.ktor:ktor-client-okhttp:2.3.2")
            }
        }

        // Create a shared iOS source set
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:1.5.5")
                implementation("io.ktor:ktor-client-darwin:2.3.2")
            }
        }

        // Link each iOS target to iosMain
        getByName("iosX64Main").dependsOn(iosMain)
        getByName("iosArm64Main").dependsOn(iosMain)
        getByName("iosSimulatorArm64Main").dependsOn(iosMain)

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }
    }
}