plugins {
    // Apply the plugins for the entire project
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Add the Google Services plugin for project-level configuration
    id("com.google.gms.google-services") version "4.4.2" apply false
}