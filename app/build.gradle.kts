@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.opalwish"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.opalwish"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
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
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.sdp.android)
    implementation (libs.ssp.android)
    implementation("io.coil-kt:coil:2.5.0")

    //viewpager2 indicator
    implementation ("me.relex:circleindicator:2.1.6")

    //Navigation component
    val nav_version = "2.5.2"
    implementation ("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation ("androidx.navigation:navigation-ui-ktx:$nav_version")

    //loading button
    implementation(libs.loading.button.android)

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.13.0")

    //circular image
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Android Ktx
    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")

    //Coroutines with firebase
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
