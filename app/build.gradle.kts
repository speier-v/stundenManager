plugins {
    alias(libs.plugins.android.application)
    //id("com.android.application") version "8.7.0" apply false
    //id("com.android.library") version "8.7.0" apply false
    //id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("com.google.gms.google-services")
}

android {
    namespace = "com.albsig.stundenmanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.albsig.stundenmanager"
        minSdk = 24
        //noinspection OldTargetApi
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.tasks)
    implementation(libs.firebase.firestore)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.support.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.material.v170)
    implementation(platform(libs.firebase.bom.v3351))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.functions)
}