package com.example.pokedex.model

sealed interface PokemonDetailsUiState {
    data object Loading : PokemonDetailsUiState
    data class Success(val pokemon: PokemonDetails) : PokemonDetailsUiState
    data class Error(val message: String) : PokemonDetailsUiState
} 