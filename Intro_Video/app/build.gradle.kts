// app/build.gradle.kts

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services") // Apply the Google Services plugin here
}

android {
    namespace = "com.example.intro_video"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.intro_video"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1") // Check for the latest version
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.cronet.embedded)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.exoplayer)
    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation("androidx.activity:activity-ktx:1.6.0")
}
