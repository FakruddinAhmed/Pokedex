package com.example.pokedex.ui.theme

import androidx.compose.ui.graphics.Color

fun getTypeColor(type: String): Color = when (type.lowercase()) {
    "normal" -> Color(0xFFA8A878)
    "fire" -> Color(0xFFF57D31)
    "water" -> Color(0xFF6493EA)
    "electric" -> Color(0xFFF9CF30)
    "grass" -> Color(0xFF74CB48)
    "ice" -> Color(0xFF98D8D8)
    "fighting" -> Color(0xFFC03028)
    "poison" -> Color(0xFFA040A0)
    "ground" -> Color(0xFFE0C068)
    "flying" -> Color(0xFFA890F0)
    "psychic" -> Color(0xFFFB5584)
    "bug" -> Color(0xFFA8B820)
    "rock" -> Color(0xFFB8A038)
    "ghost" -> Color(0xFF705898)
    "dragon" -> Color(0xFF7038F8)
    "dark" -> Color(0xFF5C5C5C)
    "steel" -> Color(0xFFB7B9D0)
    "fairy" -> Color(0xFFEE99AC)
    else -> Color(0xFF232946) // fallback to dark surface
} 