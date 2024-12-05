plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.ecocompass"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ecocompass"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "TOMTOM_API_KEY", "\"${extra["tomtomApiKey"].toString()}\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // TomTom SDK dependencies.
    implementation(libs.locationProvider)
    implementation(libs.locationSimulation)
    implementation(libs.locationMapmatched)
    implementation(libs.mapsDisplay)
    implementation(libs.navigationOnline)
    implementation(libs.navigationUi)
    implementation(libs.routePlannerOnline)
    implementation(libs.routeReplannerOnline)

    // Android dependencies.
}