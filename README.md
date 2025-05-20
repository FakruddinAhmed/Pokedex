# Pokedex Android Application

A modern Android application built with Jetpack Compose that allows users to explore and learn about Pokemon. The app uses the PokeAPI to fetch Pokemon data and provides a beautiful, intuitive interface for users to browse through Pokemon information.

## Features

- Browse Pokemon list with pagination
- Detailed Pokemon information view
- Search functionality
- Firebase Authentication
- Offline data persistence
- Modern Material 3 design
- Smooth animations and transitions

## Tech Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Clean architecture pattern
- **Retrofit** - Network calls
- **Coil** - Image loading
- **Firebase** - Authentication and Analytics
- **Navigation Compose** - Navigation handling
- **Coroutines** - Asynchronous programming
- **Material 3** - UI components and theming

## Project Structure

```
app/src/main/java/com/example/pokedex/
├── model/         # Data models and entities
├── network/       # API service and network related code
├── repository/    # Data repositories
├── ui/           # UI components and screens
├── viewmodel/    # ViewModels for different screens
├── navigation/   # Navigation setup
└── PokedexApplication.kt
```

## Prerequisites

- Android Studio Hedgehog | 2023.1.1 or later
- Kotlin 1.9.0 or later
- JDK 17 or later
- Android SDK 35 (Android 15)
- Minimum SDK version 24 (Android 7.0)

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Add your Firebase configuration file (google-services.json) to the app directory
5. Build and run the application

## Dependencies

The project uses the following major dependencies:

- androidx.compose:compose-bom:2024.02.00
- androidx.navigation:navigation-compose:2.7.7
- io.coil-kt:coil-compose:2.5.0
- com.squareup.retrofit2:retrofit:2.9.0
- com.google.firebase:firebase-bom:32.7.2

## Building and Running

1. Open the project in Android Studio
2. Wait for the Gradle sync to complete
3. Connect an Android device or start an emulator
4. Click the "Run" button or press Shift+F10

## Testing

The project includes both unit tests and instrumented tests:

- Unit tests can be run using the JUnit test runner
- Instrumented tests can be run on an Android device or emulator

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## Acknowledgments

- PokeAPI for providing the Pokemon data
- Jetpack Compose team for the amazing UI toolkit
- Firebase team for the backend services 