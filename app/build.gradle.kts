plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Versions.compileSdk)
    defaultConfig {
        applicationId = "com.movisens.rxblemovisenssample"
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlinVersion}")
    implementation("androidx.appcompat:appcompat:${Versions.androidxVersion}")
    implementation("androidx.core:core-ktx:${Versions.androidxVersion}")

    // bluetooth dependency
    implementation("com.polidea.rxandroidble2:rxandroidble:1.10.0")

    // movisens libraries
    implementation("com.github.movisens:SmartGattLib:3.0.7")
    implementation("com.github.movisens:MovisensGattLib:2.1.7")

    // architecture components
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.0.0")

    // test dependencies
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
}
