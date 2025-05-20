package com.example.pokedex.model

import androidx.compose.ui.graphics.Color

data class PokemonType(
    val name: String,
    val color: Color
)

object PokemonTypes {
    val All = PokemonType("All", Color.Gray)
    val Normal = PokemonType("Normal", Color(0xFFA8A878))
    val Fire = PokemonType("Fire", Color(0xFFF08030))
    val Water = PokemonType("Water", Color(0xFF6890F0))
    val Electric = PokemonType("Electric", Color(0xFFF8D030))
    val Grass = PokemonType("Grass", Color(0xFF78C850))
    val Ice = PokemonType("Ice", Color(0xFF98D8D8))
    val Fighting = PokemonType("Fighting", Color(0xFFC03028))
    val Poison = PokemonType("Poison", Color(0xFFA040A0))
    val Ground = PokemonType("Ground", Color(0xFFE0C068))
    val Flying = PokemonType("Flying", Color(0xFFA890F0))
    val Psychic = PokemonType("Psychic", Color(0xFFF85888))
    val Bug = PokemonType("Bug", Color(0xFFA8B820))
    val Rock = PokemonType("Rock", Color(0xFFB8A038))
    val Ghost = PokemonType("Ghost", Color(0xFF705898))
    val Dragon = PokemonType("Dragon", Color(0xFF7038F8))
    val Dark = PokemonType("Dark", Color(0xFF705848))
    val Steel = PokemonType("Steel", Color(0xFFB8B8D0))
    val Fairy = PokemonType("Fairy", Color(0xFFEE99AC))

    val allTypes = listOf(
        All, Normal, Fire, Water, Electric, Grass, Ice, Fighting, Poison,
        Ground, Flying, Psychic, Bug, Rock, Ghost, Dragon, Dark, Steel, Fairy
    )

    fun fromString(name: String): PokemonType {
        return allTypes.find { it.name.equals(name, ignoreCase = true) } ?: All
    }
} 