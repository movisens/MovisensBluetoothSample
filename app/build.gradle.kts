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
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
}
