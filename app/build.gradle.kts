plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    // Kotlin Serialization is built into the Kotlin plugin (1.9+)
    // Just add the serialization dependency below
}

android {
    namespace = "com.example.gallery"
    compileSdk = 34
    ndkVersion = "26.1.10909125"

    defaultConfig {
        applicationId = "com.example.gallery"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        // Android Go optimizations
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.lifecycle.ExperimentalLifecycleApi"
        )
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE,NOTICE}"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    // Android Go: Split APKs by density and ABI (using new AGP 8.x syntax)
    // Note: splits{} is deprecated, using bundle{} for app bundles instead
    // For split APKs, configure in bundle block or use multiple APKs
    bundle {
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }

    // NDK Configuration for native library (disabled for now - can be enabled with correct AGP syntax)
    // externalNativeBuild {
    //     cmake {
    //         path = file("src/main/cpp/CMakeLists.txt")
    //         version = "3.22.1"
    //         // arguments syntax depends on AGP version
    //     }
    // }
}

dependencies {
    // Core Android & Material Expressive (Material 3 + Expressive)
    // Versions pinned for AGP 8.4.0 + compileSdk 34 compatibility
    val material3_version = "1.3.0-alpha01"
    val compose_version = "2024.04.00"
    val lifecycle_version = "2.7.0"
    val room_version = "2.6.1"
    val hilt_version = "2.50"
    val coil_version = "2.4.0"
    val serialization_version = "1.6.3"
    val accompanist_version = "0.32.0"
    val okhttp_version = "4.12.0"
    val coroutines_version = "1.7.3"
    val gson_version = "2.10.1"

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:$compose_version"))

    // Material Expressive - Material 3 with Expressive components
    implementation("androidx.compose.material3:material3:$material3_version")
    // material3-adaptive not released as separate artifact yet
    implementation("androidx.compose.material3:material3-window-size-class:$material3_version")

    // Compose Core
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-text")
    implementation("androidx.compose.ui:ui-util")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-graphics")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.runtime:runtime-rxjava3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.10.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.10.2")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:$hilt_version")
    ksp("com.google.dagger:hilt-compiler:$hilt_version")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room Database
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.3.0")

    // Coil Image Loading (optimized for Go)
    implementation("io.coil-kt:coil-compose:$coil_version")
    implementation("io.coil-kt:coil-gif:$coil_version")
    // coil-network-okhttp might not be needed separately in Coil 2.x (OkHttp is default)
    // implementation("io.coil-kt:coil-network-okhttp:$coil_version")

    // Coroutines & Flow (Flow is included in kotlinx-coroutines-core)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    // Kotlinx DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")

    // Gson for TypeConverters
    implementation("com.google.code.gson:gson:$gson_version")

    // Accompanist (permissions, system UI controller)
    implementation("com.google.accompanist:accompanist-permissions:$accompanist_version")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist_version")

    // ML Kit (on-device, lightweight for Go) - versions may need adjustment
    // implementation("com.google.mlkit:face-detection:16.1.6")
    // implementation("com.google.mlkit:image-labeling-custom:16.0.8")
    // implementation("com.google.mlkit:object-detection-custom:16.0.0")

    // TensorFlow Lite (for custom models like FaceNet)
    // implementation("org.tensorflow:tensorflow-lite:2.16.1")
    // implementation("org.tensorflow:tensorflow-lite-gpu:2.16.1")
    // implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // ExoPlayer for video
    // implementation("com.google.android.exoplayer:exoplayer-core:2.20.0")
    // implementation("com.google.android.exoplayer:exoplayer-ui:2.20.0")
    // implementation("com.google.android.exoplayer:exoplayer-dash:2.20.0")
    // implementation("com.google.android.exoplayer:exoplayer-hls:2.20.0")

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.12.0")

    // Paging 3
    implementation("androidx.paging:paging-compose:3.2.1")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    // Biometric Auth (for locked folder)
    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.6.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.4")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.6.4")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}