// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.10.1" apply false // Your existing plugin
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false // Your existing plugin

    // ADD THIS LINE
    id("com.google.gms.google-services") version "4.4.1" apply false
}
