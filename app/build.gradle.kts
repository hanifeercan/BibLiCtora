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
    compileSdk = 34

    defaultConfig {
        applicationId = "com.amineaytac.biblictora"
        minSdk = 24
        targetSdk = 34
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
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    //Material
    implementation("com.google.android.material:material:1.12.0-alpha03")
    implementation("com.google.android.material:material:1.4.0")

    //SSP & SDP
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    ksp("com.google.dagger:hilt-compiler:2.47")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //Layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Picasso
    implementation("com.squareup.picasso:picasso:2.71828")

    //Paging
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")

    //TimePicker
    implementation("com.akexorcist:snap-time-picker:1.0.3")

    //Palette
    implementation("androidx.palette:palette-ktx:1.0.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")

    //Toastic
    implementation("com.github.yagmurerdogan:Toastic:1.0.1")

    //SwipeDecorator
    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")

    //PdfViewer
    implementation("com.github.DImuthuUpe:AndroidPdfViewer:3.1.0-beta.1")

    //EpubLib
    implementation("com.github.psiegman.epublib:epublib-core:69ac6b0"){
        exclude(group = "xmlpull", module = "xmlpull")
    }
}