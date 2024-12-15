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
    implementation(libs.camera.core)
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
    //ML kit dependencies

    // Object detection feature with bundled default classifier
    implementation("com.google.mlkit:object-detection:17.0.2")

    // Object detection feature with custom classifier support
    implementation("com.google.mlkit:object-detection-custom:17.0.2")

    // Image labeling
    implementation("com.google.mlkit:image-labeling:17.0.9")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-image-labeling:16.0.8'

    // Image labeling custom
    implementation("com.google.mlkit:image-labeling-custom:17.0.3")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-image-labeling-custom:16.0.0-beta5'

    // -------------------------------------------------------

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:27.1-android")

    // For how to setup gradle dependencies in Android X, see:
    // https://developer.android.com/training/testing/set-up-project#gradle-dependencies
    // Core library
    androidTestImplementation("androidx.test:core:1.4.0")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")

    // Assertions
    androidTestImplementation("androidx.test.ext:junit:1.1.3")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-livedata:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.3.1")

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.annotation:annotation:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // CameraX
    implementation("androidx.camera:camera-camera2:1.0.0-SNAPSHOT")
    implementation("androidx.camera:camera-lifecycle:1.0.0-SNAPSHOT")
    implementation("androidx.camera:camera-view:1.0.0-SNAPSHOT")

    // On Device Machine Learnings
    implementation("com.google.android.odml:image:1.0.0-beta1")
}