# PokeKuy

A Submission Android app that displays Pokemon data from PokeAPI with offline access capability and user authentication.

## Features

- **User Authentication**: Login and register functionality with local database storage
- **Pokemon List**: Display a paginated list of Pokemon from the PokeAPI
- **Pokemon Details**: View detailed information about each Pokemon including abilities
- **Search Functionality**: Search for Pokemon by name
- **Offline Access**: Cache Pokemon data for offline viewing
- **Clean Architecture**: Follows Clean Architecture principles with separation of concerns

## Project Structure

The project follows the Clean Architecture pattern with the following modules:

- **Data Layer**: Handles data operations, API communication, and local database
- **Domain Layer**: Contains business logic and use cases
- **Presentation Layer**: UI components and ViewModels

## Technical Stack

- **Language**: Kotlin
- **UI**: XML-based layouts with Material Design components
- **Architecture**: MVVM + Clean Architecture
- **Networking**: Retrofit for API communication
- **Concurrency**: Kotlin Coroutines for asynchronous operations
- **Dependency Injection**: Koin
- **Database**: SQLite for local caching and user data

## API

This application uses the [PokeAPI](https://pokeapi.co/) for Pokemon data.

## License

This project is licensed under the MIT License - see the LICENSE file for details.