package com.example.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.model.PokemonDetailsUiState
import com.example.pokedex.repository.LikeRepository
import com.example.pokedex.repository.PokemonRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailViewModel : ViewModel() {
    private val pokemonRepository = PokemonRepository()
    private val likeRepository = LikeRepository()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<PokemonDetailsUiState>(PokemonDetailsUiState.Loading)
    val uiState: StateFlow<PokemonDetailsUiState> = _uiState

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    fun loadPokemonDetails(pokemonName: String) {
        viewModelScope.launch {
            try {
                Log.d("DetailViewModel", "Starting to load Pokemon details for: $pokemonName")
                _uiState.value = PokemonDetailsUiState.Loading
                val details = pokemonRepository.getPokemonDetail(pokemonName)
                Log.d("DetailViewModel", "Successfully loaded Pokemon details: ${details.name}")
                Log.d("DetailViewModel", "Stats count: ${details.stats.size}")
                details.stats.forEach { stat ->
                    Log.d("DetailViewModel", "Stat: ${stat.stat.name}, Base: ${stat.baseStat}")
                }
                Log.d("DetailViewModel", "Types count: ${details.types.size}")
                details.types.forEach { type ->
                    Log.d("DetailViewModel", "Type: ${type.type.name}")
                }
                Log.d("DetailViewModel", "Abilities count: ${details.abilities.size}")
                details.abilities.forEach { ability ->
                    Log.d("DetailViewModel", "Ability: ${ability.ability.name}")
                }
                _uiState.value = PokemonDetailsUiState.Success(details)
                Log.d("DetailViewModel", "Updated UI state to Success")
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error loading Pokemon details", e)
                _uiState.value = PokemonDetailsUiState.Error(
                    e.message ?: "Failed to load Pokemon details"
                )
            }
        }
    }

    fun checkIfPokemonIsLiked(pokemonName: String) {
        viewModelScope.launch {
            try {
                // Observe liked Pokemon changes
                likeRepository.observeLikedPokemon()
                    .collect { likedIds ->
                        _isLiked.value = likedIds.contains(pokemonName)
                    }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error checking if Pokemon is liked", e)
                _isLiked.value = false
            }
        }
    }

    fun toggleLikePokemon(pokemonName: String) {
        viewModelScope.launch {
            try {
                if (auth.currentUser == null) {
                    throw Exception("Please log in to like Pokemon")
                }
                likeRepository.toggleLikePokemon(pokemonName, pokemonName)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error toggling Pokemon like", e)
                // Handle error (you might want to show a snackbar or some other UI feedback)
            }
        }
    }
} 