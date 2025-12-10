plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // REQUIRED: The Google Services plugin for Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ahmed.assignment"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ahmed.assignment"
        minSdk = 24
        targetSdk = 35
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

    // FIX: Removed 'buildFeatures { viewBinding = true }' completely.
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- FIREBASE FIX ---
    // Instead of using the BoM (which is causing the version error),
    // we will hardcode the stable versions directly.

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics:22.1.2")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth:23.1.0")
    // --------------------

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}