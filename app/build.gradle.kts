import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.github.slznvk.nework"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.slznvk.nework"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    defaultConfig {
        multiDexEnabled = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())
            val serverKey: String = p.getProperty("SERVER_API_KEY")
            buildConfigField("String", "SERVER_API_KEY", "\"$serverKey\"")


            val mapKitKey: String = p.getProperty("MAPKIT_API_KEY")
            buildConfigField("String", "MAPKIT_API_KEY", "\"$mapKitKey\"")
        }
        debug {
            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())
            val serverKey: String = p.getProperty("SERVER_API_KEY")
            buildConfigField("String", "SERVER_API_KEY", "\"$serverKey\"")

            val mapKitKey: String = p.getProperty("MAPKIT_API_KEY")
            buildConfigField("String", "MAPKIT_API_KEY", "\"$mapKitKey\"")
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
        viewBinding = true
        buildConfig = true
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.paging.runtime)
    implementation(libs.imagepicker)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.glide)
    implementation(libs.adapterdelegates4.kotlin.dsl)
    implementation(libs.adapterdelegates4.kotlin.dsl.viewbinding)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.maps.mobile)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}