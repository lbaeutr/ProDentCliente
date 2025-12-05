plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

android {
    namespace = "dev.luisbaena.prodentclient"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.luisbaena.prodentclient"
        minSdk = 24
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation.core.lint)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6") // Testing de navegación
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //== Authenticathion ==

    // Retrofit para API REST
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Gson para serialización JSON --> Usado en el DataStore de preferencias pq retrofit no puede procesar datos complejos
    implementation("com.google.code.gson:gson:2.10.1")
    // Para manejo de tokens JWT
    implementation("com.auth0.android:jwtdecode:2.0.2")
    // DataStore para guardar tokens
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //== Architecture + MVVM ==

    // Hilt - Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Core Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // SWIPE REFRESH  PULL TO REFRESH -> ESTA IMPLEMENTACION ES PARA EL PULL TO REFRESH
    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")

    // Coil para carga de imágenes
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ZXing
    implementation("com.journeyapps:zxing-android-embedded:4.3.0") // Para escanear códigos QR
    implementation("com.google.zxing:core:3.5.2") // Para generar códigos QR

    // iText para generar PDFs
    implementation("com.itextpdf:itext7-core:7.2.5")
}