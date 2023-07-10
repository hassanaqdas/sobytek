plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 33
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.sobytek.erpsobytek"
        minSdk = 21
        targetSdk = 33
        versionCode = 22
        versionName = "1.22"
        multiDexEnabled = true
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

    buildFeatures{
        dataBinding = true
        viewBinding = true
    }

//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    // ANDROID X RECYCLER VIEW LIBRARY
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    // ANDROID X CARD VIEW LIBRARY
    implementation("androidx.cardview:cardview:1.0.0")

    // MULTI DEX DEPENDENCY
    implementation("androidx.multidex:multidex:2.0.1")

    // RETROFIT NETWORK LIBRARY
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // GSON LIBRARY
    implementation("com.google.code.gson:gson:2.8.6")

    // LIFECYCLE AND VIEW MODEL DEPENDENCY
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation ("android.arch.lifecycle:extensions:1.1.1")

    // BIOMETRIC AUTHENTICATION LIBRARY
    implementation("androidx.biometric:biometric:1.2.0-alpha03")

    // BARCODE SCANNING LIBRARY
    implementation("me.dm7.barcodescanner:zxing:1.9.8")

    // FIREBASE LIBRARY
    implementation("com.google.firebase:firebase-analytics-ktx:19.0.0")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.1.0")

    // VISION BARCODE SCANNER
    // Vision API Scanner
    // CameraX core library using the camera2 implementation

    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation ("androidx.camera:camera-core:1.0.0")
    implementation ("androidx.camera:camera-camera2:1.0.0")
    // If you want to additionally use the CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:1.0.0")
    // If you want to additionally use the CameraX View class
    implementation ("androidx.camera:camera-view:1.0.0-alpha25")
    // If you want to additionally use the CameraX Extensions library
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha25")
    implementation ("com.google.mlkit:barcode-scanning:16.1.2")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:16.1.5")
}