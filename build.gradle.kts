buildscript {
    dependencies {
        classpath("de.nanogiants:android-versioning:2.4.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
    id("de.nanogiants.android-versioning") version "2.4.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.5" apply false
}