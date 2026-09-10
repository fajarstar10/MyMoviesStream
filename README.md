# Movie Explorer App

A mobile native application to explore movies using [The Movie Database (TMDb) API](https://www.themoviedb.org/documentation/api). Application built with modern mobile architecture patterns to deliver smooth user experience and high maintainability.

---

## 📱 Features & User Stories

- [x] **Movie Genres Screen**: Displays a clean list of official TMDB movie genres.
- [x] **Discover Movies by Genre**: Lists popular and trending movies categorized by selected genre.
- [x] **Movie Detail Screen**: Displays primary information about the movie (Title, Poster, Overview, Rating, Release Date, Cast, etc.).
- [x] **User Reviews**: Displays user reviews and ratings for a selected movie.
- [x] **YouTube Trailer**: Integrates YouTube player to watch official movie trailers directly in the app.
- [x] **Endless Scrolling (Pagination)**: Implemented infinite scrolling pagination on movie lists and user reviews for efficient data loading.
- [x] **Error & State Handling**: Robust error handling covering positive cases (success loading, empty states) and negative cases (no internet connection, API timeout, failed requests).

---

## 🏗️ Architecture & Tech Stack
* **Language**: Kotlin
* **UI Framework**: Jetpack Compose / XML Layouts
* **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
* **Asynchronous / Concurrency**: Kotlin Coroutines & Flow
* **Dependency Injection**: Hilt / Koin
* **Networking**: Retrofit2 + OkHttp3 + Moshi/Gson
* **Pagination**: Paging 3 Library (Endless Scrolling)
* **Image Loading**: Coil / Glide
* **Video Player**: YouTube Player SDK / AndroidView Integration
* **Testing**: JUnit5, MockK / Mockito, Coroutines Test

---
