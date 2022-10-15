# GitHub Users App
A simple Github users App that retrieves GitHub users by using GitHub open REST API and then displays the selected user's profile.

# Intro
This simply load github users list from github open [REST API](https://docs.github.com/en/rest/users/users) stores it in persistence storage **Room Db**. Click on any user item the app will navigate you to simple user profile screen which built with [Jetpack Compose ❤️](https://developer.android.com/jetpack/compose).
- Simple Github users list
- Pagination 
- Offline Caching
- Always Background synchronized 
- Profile screen built with Jetpack Compose

## Tech Stack 🛠
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that sequentially emits values.
  - [Mutable State Flow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - for emit UI state with data.
  - [Mutable Shared Flow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - for emit one time event.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Jetpack compose support for Profile Screen
- **Clean Architecture on Top of MVVM Architecture**
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
  - [Room](https://developer.android.com/topic/libraries/architecture/room) - SQLite object mapping library.
- [Dependency Injection](https://developer.android.com/training/dependency-injection) - 
  - [Hilt-Dagger](https://dagger.dev/hilt/) - Standard library to incorporate Dagger dependency injection into an Android application.
  - [Hilt-ViewModel](https://developer.android.com/training/dependency-injection/hilt-jetpack) - DI for injecting `ViewModel`.
- [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.
- [Single Reactive Network Response Handle](https://github.com/ImtiazDipto01/GithubUsers/blob/master/app/src/main/java/com/imtiaz/githubuserstest/core/extensions/NetworkResponseHandler.kt) - A simple Reactive Data streaming process from Network Layer to UI layer using Kotlin Flow. 
- [Gson](https://github.com/google/gson) - Gson is a Java library that can be used to convert Java Objects into their JSON representation.
- [Glide](https://github.com/bumptech/glide) - Glide is for fast image loading from network URL
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.
- [Junit5 Unit Testing](https://junit.org/junit5/) - Android Local Unit Testing with Junit5
- [OkHttp MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) - Android Local unit testing on Api calls using MockWebServer


## App UI
![](https://github.com/ImtiazDipto01/GithubUsers/blob/master/screenshot_1.png) ![](https://github.com/ImtiazDipto01/GithubUsers/blob/master/screenshot_2.png)

