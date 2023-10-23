plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("com.google.devtools.ksp")//.version("1.9.10-1.0.13")
//    id("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.exmpl.btcwallet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.exmpl.btcwallet"
        minSdk = 26
        targetSdk = 34
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

/*kapt {
    correctErrorTypes = true
}*/

dependencies {

    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    val navigationVer = "2.7.4"
    implementation ("androidx.navigation:navigation-fragment-ktx:$navigationVer")
    implementation ("androidx.navigation:navigation-ui-ktx:$navigationVer")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.datastore:datastore-preferences-core:1.0.0")

    implementation ("androidx.activity:activity-ktx:1.8.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    implementation ("com.squareup.okhttp3:okhttp:4.11.0")

    //          Hilt
    val hiltVer = "2.48.1"
    implementation ("com.google.dagger:hilt-android:$hiltVer")
    ksp ("com.google.dagger:hilt-compiler:$hiltVer")
//    kapt ("com.google.dagger:hilt-compiler:$hiltVer")

    implementation ("org.bitcoinj:bitcoinj-core:0.16.2")

    // Moshi
    val moshiVer = "1.15.0"
    implementation ("com.squareup.moshi:moshi-kotlin:$moshiVer")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVer")

    implementation ("androidx.paging:paging-runtime-ktx:3.2.1")

    // Test
    testImplementation ("junit:junit:4.13.2")


    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}