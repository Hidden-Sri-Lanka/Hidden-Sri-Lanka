plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.s23010526.hiddensrilanka"
    compileSdk = 34  // Change from 35 to 34 for better compatibility

    defaultConfig {
        applicationId = "com.s23010526.hiddensrilanka"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11  // Update from VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_11  // Update from VERSION_1_8
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

    // --- Firebase Setup ---
    // Firebase BoM -- Bill of Materials--
    // This line manages all Firebase library versions.
    implementation(platform(libs.firebase.bom))

    // Versions are handled by the BoM so dont need to specify them here.
    implementation(libs.firebase.auth)       // For login system
    implementation(libs.firebase.database)   // For login system
    implementation(libs.firebase.firestore)  // For storing location data
    implementation(libs.firebase.storage)    // For storing images

    // --- Google Play Services ---
    implementation("com.google.android.gms:play-services-location:21.0.1") // For location services
    implementation("com.google.android.gms:play-services-auth:20.7.0") // For Google Sign-In
    implementation("com.google.android.gms:play-services-maps:18.2.0") // For Google Maps

    // --- CardView for unified design system ---
    implementation("androidx.cardview:cardview:1.0.0")

    // --- Image Loading ---
    implementation(libs.glide)               // For loading images
    annotationProcessor(libs.glide.compiler) // For Glide annotation processing

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
