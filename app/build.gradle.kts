plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.s23010526.hiddensrilanka"
    compileSdk = 35

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

    // --- Firebase Setup ---
    // Firebase BoM -- Bill of Materials--
    // This line manages all Firebase library versions.
    implementation(platform(libs.firebase.bom))

    // Versions are handled by the BoM so  dont need to  specify them here.
    implementation(libs.firebase.auth)       // For  login system
    implementation(libs.firebase.database)   // For login system
    implementation(libs.firebase.firestore)  // For location content


    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Google Play Servicesfor location
    implementation(libs.play.services.location)


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

