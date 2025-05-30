# <p align="center"> 📖 BibLiCtora</p> 

<p align="center">
  <img src="https://github.com/user-attachments/assets/1d5090fa-87cc-4f43-a902-998bf2fd2083" alt="Biblictora"/>
</p>

<div align="center">

⭐️ The app name <strong>BibLiCtora</strong> is a creative blend inspired by three Spanish words:<br>
<strong>Biblioteca</strong> (library)<br>
<strong>Libro</strong> (book)<br>
<strong>Lectora</strong> (female reader)<br><br>

This name reflects our app’s focus on providing a rich, reader-friendly library experience.

</div>

## 📱 Screenshots


| ![](https://github.com/user-attachments/assets/4e57c358-45d2-4e82-9c5e-5d924b40ede8) | ![onboard](https://github.com/user-attachments/assets/c63094fa-344c-4daa-9cbb-86beae3ceeb6) | ![](https://github.com/user-attachments/assets/5dc084d3-3e1a-43b6-92db-0b2ff6288dec) |
|---|---|---|
| ![](https://github.com/user-attachments/assets/f4367955-d475-4657-a21a-53926dba4928) | ![](https://github.com/user-attachments/assets/7734bfa3-24a0-45a8-8af9-e4c49ff693f3) | ![](https://github.com/user-attachments/assets/26073cac-fa94-4158-aed5-6f9c9bbfccf1)|
|  ![](https://github.com/user-attachments/assets/b999f70b-2fd0-461f-ac20-41344c642f1a) | ![](https://github.com/user-attachments/assets/7b60e27e-09f9-46f8-a9ca-1d1e74ea79cb) | ![](https://github.com/user-attachments/assets/a7369a27-1537-4423-8877-d3298bb56ce7)|
|  ![](https://github.com/user-attachments/assets/402517ee-d335-4aba-bd67-c923b7b3c380) |  ![](https://github.com/user-attachments/assets/db14e117-caf4-4702-af9e-c59b2a3980c6)  | ![](https://github.com/user-attachments/assets/d187846a-d70e-4ebf-949c-43eb44976797) |





## 🏗️ Architecture 

```
app/
├── core/
│   ├── common/            # Common auxiliary classes
│   ├── data/              # Data layer, repositories
│   ├── database/          # Room database, entity and DAO classes
│   ├── domain/            # Use cases, business logic
│   ├── network/           # API interfaces, network layer
│
└── ui/
    ├── basereading/       # The basic structure or infrastructure of the reading screen
    ├── customview/        # Custom view components used in the application
    └── detail/            # Book detail screen
    ├── discover/          # Discover screen, book recommendations
    ├── epubviewer/        # Screen to view books in EPUB format
    └── favorite/          # Favorite and Quote operations
    ├── favoritebooks/     # Favorite books screen
    ├── home/              # Home screen, general content
    └── mybooks/           # Screen showing books that the user has uploaded from the device
    ├── onboard/           # Onboarding screens seen at first startup
    ├── pdfviewer/         # Screen for viewing books in PDF format
    └── quotes/            # Quotes screen
    ├── reading/           # Reading screen (book reading experience)
    ├── readinglist/       # Reading list screen (books to read, etc.)
    └── splash/            # Application splash screen (Splash Screen)
    ├── widget/            # In-app widget components
│
└── util/
```

## 🛠️ Tech Stack

### Core
- [**Kotlin**](https://kotlinlang.org/) : Primary programming language
- [**Coroutines**](https://github.com/Kotlin/kotlinx.coroutines) & [**Flow**](https://developer.android.com/kotlin/flow) : Asynchronous programming
- [**Hilt**](https://developer.android.com/training/dependency-injection/hilt-android) : Dependency injection
- [**Room**](https://developer.android.com/training/data-storage/room) : Local database
- [**MVVM**](https://developer.android.com/topic/architecture) : Presentation layer pattern

### UI
- [**Retrofit**](https://square.github.io/retrofit/) : Type-safe HTTP client for Android used to handle API requests.
- [**OkHttp**](https://square.github.io/okhttp/) : Efficient HTTP client used with Retrofit for handling network calls.
- [**Navigation**](https://developer.android.com/guide/navigation) : Navigation to manage fragments
- [**LiveData**](https://developer.android.com/topic/libraries/architecture/livedata) : Lifecycle-aware data holder that updates UI components only when they're active.
- [**ViewBinding**](https://developer.android.com/topic/libraries/architecture/livedata) : View Binding a generated binding class is created for each XML layout file in your app.
- [**Picasso**](https://square.github.io/picasso/) : Image loading and caching library for displaying book covers and images.
- [**Paging**](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) : Supports loading paginated data efficiently in RecyclerViews.
- [**Palette**](https://developer.android.com/develop/ui/views/graphics/palette-colors) : Extracts prominent colors from images to dynamically style the UI.
- [**Toastic**](https://github.com/yagmurerdogan/Toastic) : Customizable toast messages to improve user feedback and interaction.
- [**Swipe Decorator**](https://github.com/xabaras/RecyclerViewSwipeDecorator) : Provides decoration (icons, background) for swipe actions in RecyclerView.
- [**PdfViewer**](https://mvnrepository.com/artifact/com.github.DImuthuUpe/AndroidPdfViewer/3.1.0-beta.1) : Allows opening and reading PDF-format books inside the app.
- [**Epublib**](https://github.com/psiegman/epublib) : Parses and renders EPUB-format books for reading.
- [**CircleImageView**](https://github.com/hdodenhof/CircleImageView) : Displays circular profile or book cover images.
- [**Page Indicator**](https://github.com/Gozirin/Dots.Indicator) : Visual indicator for current page or screen in a ViewPager or onboarding flow.

## ✨ Features

📚 Real-time online book reading  
🌐 Filter books by over 10 different languages  
❤️ Add books to your favorites for quick access  
📝 View your saved quotes on a dedicated quotes page  
🛠️ Customize reading screen with font size and background color options  
📁 Open and read books stored on your local device  
⏰ Set reading reminder notifications for specific times  
🏠 Add a home screen widget for quick access  
🌙 Support for dark/light mode 

## 📦 Dependency
```
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

    // Material
    implementation("com.google.android.material:material:1.12.0-alpha03")
    implementation("com.google.android.material:material:1.4.0")

    // SSP & SDP
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

    // Layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Picasso
    implementation("com.squareup.picasso:picasso:2.71828")

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")

    // Palette
    implementation("androidx.palette:palette-ktx:1.0.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")

    // Toastic
    implementation("com.github.yagmurerdogan:Toastic:1.0.1")

    // SwipeDecorator
    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")

    // PdfViewer
    implementation("com.github.DImuthuUpe:AndroidPdfViewer:3.1.0-beta.1")

    // EpubLib
    implementation("com.github.psiegman.epublib:epublib-core:69ac6b0") {
        exclude(group = "xmlpull", module = "xmlpull")
    }

    // CircleImageView
    implementation("com.github.hdodenhof:CircleImageView:v3.1.0")

    // Page Indicator
    implementation("com.tbuonomo:dotsindicator:4.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
}
```

app build.gradle:

```
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

buildFeatures {
      viewBinding = true
 }
```
project build.gradle:

```
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false
}
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👩 Authors & Contact

💌 **We’d love to hear your thoughts, suggestions, or questions about the app — feel free to reach out to us via email.**

- **Hanife ERCAN**  
  🔗 [GitHub](https://github.com/hanifeercan) <br>
  🔗 [LinkedIn](https://www.linkedin.com/in/hanifeercan/)  <br>
  ✉️ hanifeercan02@outlook.com  

- **Amine AYTAÇ**  
  🔗 [GitHub](https://github.com/amineytc) <br>
  🔗 [LinkedIn](https://www.linkedin.com/in/amine-aytac/)  <br>
  ✉️ aytacamine633@gmail.com 
