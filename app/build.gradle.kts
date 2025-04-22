plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("com.google.devtools.ksp")
    id ("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" // this version matches your Kotlin version
    kotlin("plugin.serialization") version "2.0.21"
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
        compose = true
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

dependencies {

    val composeBom = platform("androidx.compose:compose-bom:2025.04.00")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-compose")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.10.1")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation ("androidx.navigation:navigation-compose")

    implementation ("androidx.core:core-ktx:1.16.0")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    val navigationVer = "2.8.9"
    implementation ("androidx.navigation:navigation-fragment-ktx:$navigationVer")
    implementation ("androidx.navigation:navigation-ui-ktx:$navigationVer")

    implementation ("androidx.datastore:datastore-preferences:1.1.4")

    implementation ("androidx.activity:activity-ktx:1.10.1")
    implementation ("androidx.fragment:fragment-ktx:1.8.6")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")                      // ?
    implementation ("androidx.recyclerview:recyclerview:1.4.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")

    //          Hilt
    val hiltVer = "2.56.1"
    implementation ("com.google.dagger:hilt-android:$hiltVer")
    ksp ("com.google.dagger:hilt-compiler:$hiltVer")
//    kapt ("com.google.dagger:hilt-compiler:$hiltVer")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation ("org.bitcoinj:bitcoinj-core:0.16.5")

    // Moshi
    val moshiVer = "1.15.2"
    implementation ("com.squareup.moshi:moshi-kotlin:$moshiVer")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVer")

    val pagingVer = "3.3.6"
    implementation ("androidx.paging:paging-runtime-ktx:$pagingVer")
    implementation ("androidx.paging:paging-compose:$pagingVer")

    // Test
    testImplementation ("junit:junit:4.13.2")

    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")

    androidTestImplementation(composeBom)
}