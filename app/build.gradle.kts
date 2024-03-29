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

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
            setIncludeAndroidResources(true)
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlinVersion}")

    implementation("androidx.appcompat:appcompat:${Versions.androidxVersion}")
    implementation("androidx.core:core-ktx:${Versions.androidxVersion}")
    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha06")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.1.0-alpha07")

    // simplified android permissions
    implementation("com.karumi:dexter:5.0.0")

    // bluetooth dependency
    implementation("com.polidea.rxandroidble2:rxandroidble:1.10.0")

    // RxJava 2
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.8")

    // movisens libraries
    implementation("com.github.movisens:SmartGattLib:3.0.7")
    implementation("com.github.movisens:MovisensGattLib:2.1.7")

    // architecture components
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.0.0")

    //DI dependencies to get view models testable
    implementation("org.koin:koin-android-viewmodel:2.0.1")

    // test dependencies
    testImplementation("junit:junit:4.12")
    testImplementation("com.polidea.rxandroidble2:mockclient:1.10.0")
    // must be used because of https://github.com/Polidea/RxAndroidBle/issues/262
    testImplementation("org.robolectric:robolectric:4.3")
    testImplementation("org.mockito:mockito-core:2.24.5")

    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:rules:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("org.mockito:mockito-android:2.24.5")
}
