package com.example.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.model.Pokemon
import com.example.pokedex.repository.LikeRepository
import com.example.pokedex.repository.PokemonRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {
    private val pokemonRepository = PokemonRepository()
    private val likeRepository = LikeRepository()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _likedPokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val likedPokemonList = _likedPokemonList.asStateFlow()

    init {
        observeLikedPokemon()
    }

    private fun observeLikedPokemon() {
        viewModelScope.launch {
            likeRepository.observeLikedPokemon()
                .catch { e -> 
                    Log.e("FavoritesViewModel", "Error observing liked Pokemon", e)
                    _error.value = e.message 
                }
                .collect { likedIds ->
                    loadLikedPokemon(likedIds)
                }
        }
    }

    private suspend fun loadLikedPokemon(likedIds: Set<String>) {
        try {
            _isLoading.value = true
            val likedPokemon = likedIds.mapNotNull { id ->
                try {
                    val pokemon = pokemonRepository.getPokemonList()
                        .find { it.id.toString() == id }
                    if (pokemon != null) {
                        val details = pokemonRepository.getPokemonDetail(pokemon.name)
                        pokemon.copy(types = details.types)
                    } else null
                } catch (e: Exception) {
                    Log.e("FavoritesViewModel", "Error loading Pokemon $id", e)
                    null
                }
            }
            _likedPokemonList.value = likedPokemon
        } catch (e: Exception) {
            Log.e("FavoritesViewModel", "Error loading liked Pokemon", e)
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    fun toggleLikePokemon(pokemonId: String, name: String) {
        viewModelScope.launch {
            try {
                likeRepository.toggleLikePokemon(pokemonId, name)
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error toggling Pokemon like", e)
                _error.value = "Failed to toggle like: ${e.message}"
            }
        }
    }
} 