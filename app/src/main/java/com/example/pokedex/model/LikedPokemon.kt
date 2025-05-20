package com.example.pokedex.model

data class LikedPokemon(
    val pokemonId: String = "",
    val name: String = "",
    val timestamp: Long = System.currentTimeMillis()
) 