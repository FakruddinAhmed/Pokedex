package com.example.pokedex.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Detail : Screen("detail/{pokemonName}") {
        fun createRoute(pokemonName: String) = "detail/$pokemonName"
    }
    object Favorites : Screen("favorites")
} 