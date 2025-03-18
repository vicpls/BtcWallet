plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("com.google.devtools.ksp")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.exmpl.btcwallet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.exmpl.btcwallet"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("androidx.core:core-ktx:1.15.0")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.2.1")

    val navigationVer = "2.8.9"
    implementation ("androidx.navigation:navigation-fragment-ktx:$navigationVer")
    implementation ("androidx.navigation:navigation-ui-ktx:$navigationVer")

    implementation ("androidx.datastore:datastore-preferences:1.1.3")
    implementation ("androidx.datastore:datastore-preferences-core:1.1.3")

    implementation ("androidx.activity:activity-ktx:1.10.1")
    implementation ("androidx.fragment:fragment-ktx:1.8.6")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.4.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")

    //          Hilt
    val hiltVer = "2.51.1"
    implementation ("com.google.dagger:hilt-android:$hiltVer")
    ksp ("com.google.dagger:hilt-compiler:$hiltVer")
//    kapt ("com.google.dagger:hilt-compiler:$hiltVer")

    implementation ("org.bitcoinj:bitcoinj-core:0.16.2")

    // Moshi
    val moshiVer = "1.15.1"
    implementation ("com.squareup.moshi:moshi-kotlin:$moshiVer")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVer")

    implementation ("androidx.paging:paging-runtime-ktx:3.3.6")

    // Test
    testImplementation ("junit:junit:4.13.2")


    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")
}