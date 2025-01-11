plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id ("kotlin-parcelize")

}

android {
    namespace = "com.meditrust.findadoctor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.meditrust.findadoctor"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations += listOf("en", "bn")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Views/Fragments integration
    val nav_version = "2.8.0"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.firebase:firebase-storage")
    // ViewModel +  LiveData+ Lifecycles only (without ViewModel or LiveData)+ Saved state module for ViewModel
    val lifecycle_version = "2.6.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    //Datastore
    val datastore_version = "1.1.1"
    implementation("androidx.datastore:datastore-preferences:$datastore_version")

    // Dagger - Hilt
    val dagger_hilt_version = "2.45"
    implementation("com.google.dagger:hilt-android:$dagger_hilt_version")
    kapt("com.google.dagger:hilt-compiler:$dagger_hilt_version")

    //Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.5.0-alpha02"
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")



}


