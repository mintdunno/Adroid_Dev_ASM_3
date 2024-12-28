plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.minh.payday"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.minh.payday"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // AndroidX & Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.core)
    implementation(libs.constraintlayout)


    // ---------------------------------------------------------
    // Firebase (using BOM 33.7.0 to align all versions)
    // ---------------------------------------------------------
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    // Messaging, Storage, etc.
     implementation(libs.firebase.messaging)
     implementation(libs.firebase.storage)
    
    // ---------------------------------------------------------
    // Image Loading & UI
    // ---------------------------------------------------------
    implementation(libs.glide)
    implementation(libs.activity)
    annotationProcessor(libs.compiler)
    implementation(libs.circleimageview)
    
    // ---------------------------------------------------------
    // Optional Payment Libraries
    // ---------------------------------------------------------
     implementation("com.android.billingclient:billing:7.1.1")
     implementation("com.stripe:stripe-android:20.22.2")
     implementation("com.paypal.sdk:paypal-android-sdk:2.16.0")
    
    // ---------------------------------------------------------
    // Testing
    // ---------------------------------------------------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}