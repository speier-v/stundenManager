// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    //id("com.android.application") version "8.7.0" apply false
    //id("com.android.library") version "8.7.0" apply false
    //id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}