plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.amineaytac.biblictora"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.amineaytac.biblictora"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")

    //Material
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.material:material:1.12.0")

    //SSP & SDP
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.intuit.sdp:sdp-android:1.1.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    ksp("com.google.dagger:hilt-compiler:2.56.2")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Coil (replacing Picasso)
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-base:2.7.0")

    //Paging
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")

    //Palette
    implementation("androidx.palette:palette-ktx:1.0.0")

    // Room
    implementation("androidx.room:room-runtime:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")

    //Toastic
    implementation("com.github.yagmurerdogan:Toastic:1.0.1")

    //SwipeDecorator
    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")

    //PdfViewer
    implementation("com.github.DImuthuUpe:AndroidPdfViewer:3.1.0-beta.1")

    //EpubLib
    implementation("com.github.psiegman.epublib:epublib-core:69ac6b0") {
        exclude(group = "xmlpull", module = "xmlpull")
    }

    //CircleImageView
    implementation("com.github.hdodenhof:CircleImageView:v3.1.0")

    //page indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    //Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0")
}