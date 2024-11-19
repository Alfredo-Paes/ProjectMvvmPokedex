plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.alfredopaesdaluz.projectmvvmpokedex"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alfredopaesdaluz.projectmvvmpokedex"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }



    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android.v2511)
    implementation(libs.androidx.navigation.testing)
    testImplementation(libs.androidx.ui.test.junit4.android)
    kapt(libs.hilt.android.compiler.v2511)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.timber)

    implementation(libs.androidx.palette.ktx)

    implementation(libs.coil.compose)

    implementation(libs.androidx.ui.text.google.fonts)

    //implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:<version>")
    debugImplementation("androidx.compose.ui:ui-test-manifest:<version>")

    testImplementation("org.robolectric:robolectric:4.10")

    testImplementation("org.slf4j:slf4j-simple:2.0.9")

    testImplementation(libs.mockk) // MockK para mocks em Kotlin

    testImplementation(libs.kotlinx.coroutines.test) // Para testar corrotinas

    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}