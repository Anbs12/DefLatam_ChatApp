plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.deflatam_chatapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.deflatam_chatapp"
        minSdk = 29
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
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ===== KOTLIN COROUTINES =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // ===== LIFECYCLE =====
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")

    // ===== HILT - INYECCIÓN DE DEPENDENCIAS =====
    implementation("com.google.dagger:hilt-android:2.57")
    ksp("com.google.dagger:hilt-android-compiler:2.57")

    // ===== ROOM - BASE DE DATOS LOCAL =====
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    // ===== NETWORKING =====
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // ===== UI COMPONENTS =====
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // ===== IMAGE LOADING =====
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")

    // ===== JSON =====
    implementation("com.google.code.gson:gson:2.13.1")

    // Importar Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))

    // Analytics (recomendado para la mayoría de las apps)
    implementation("com.google.firebase:firebase-analytics-ktx:22.5.0")

    // Authentication (para inicio de sesión y gestión de usuarios)
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")

    // Cloud Firestore (base de datos NoSQL en tiempo real)
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")

    // Realtime Database (otra opción de base de datos en tiempo real)
    // implementation("com.google.firebase:firebase-database-ktx")

    // Cloud Storage (para almacenar archivos como imágenes, videos, etc.)
    implementation("com.google.firebase:firebase-storage-ktx:21.0.2")

    // Cloud Messaging (FCM - para notificaciones push)
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")
}