import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
    id("de.nanogiants.android-versioning")
}

android {
    namespace = "com.apollo29.calendarwatch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.apollo29.calendarwatch"
        minSdk = 28
        targetSdk = 34

        versionCode = versioning.getVersionCode()
        versionName = "$version"
        setProperty("archivesBaseName", "what-calendar")

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
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    versioning {
        excludeBuildTypes = "debug"
    }
}

dependencies {
    // androidx
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.preference:preference-ktx:1.2.0")
    // todo check
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.transition:transition-ktx:1.4.1")

    // google
    implementation("com.google.guava:guava:28.2-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.material:material:1.10.0")

    // hilt
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-compiler:2.47")

    // ble
    implementation("no.nordicsemi.android.support.v18:scanner:1.6.0")
    implementation("no.nordicsemi.android:ble-ktx:2.6.0-alpha03")
    implementation("no.nordicsemi.android:ble-common:2.6.0-alpha03")
    implementation("no.nordicsemi.android:ble-livedata:2.6.0-alpha03")

    // other
    implementation("com.orhanobut:logger:2.2.0")
    implementation("com.github.fondesa:kpermissions:3.4.0")
    // todo check
    implementation("io.github.ShawnLin013:number-picker:2.4.13")
    implementation("me.relex:circleindicator:2.1.6")
}