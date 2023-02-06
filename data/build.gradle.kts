plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(mapOf("path" to ":domain")))
    implementation(Dependencies.hiltAndroid)
    kapt(Dependencies.hiltCompiler)
    implementation(Dependencies.media)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.roomRuntime)
    implementation(Dependencies.roomKtx)
    kapt(Dependencies.roomCompiler)

    testImplementation(Dependencies.truth)
    testImplementation(Dependencies.coroutinesTest)
    testImplementation(Dependencies.junit4)
    testImplementation(Dependencies.robolectric)
    testImplementation(Dependencies.roomTesting)
    androidTestImplementation(Dependencies.junit4)
    androidTestImplementation(Dependencies.testExtJunit)
    androidTestImplementation(Dependencies.testCore)
    androidTestImplementation(Dependencies.coroutinesTest)
    androidTestImplementation(Dependencies.truth)
    androidTestImplementation(Dependencies.archCoreTesting)
    androidTestImplementation(Dependencies.testRules)
}

kapt {
    correctErrorTypes = true
}