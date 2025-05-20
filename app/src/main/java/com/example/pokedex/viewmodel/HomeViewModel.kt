package com.example.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonType
import com.example.pokedex.model.PokemonTypes
import com.example.pokedex.repository.LikeRepository
import com.example.pokedex.repository.PokemonRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Dispatchers

class HomeViewModel : ViewModel() {
    private val pokemonRepository = PokemonRepository()
    private val likeRepository = LikeRepository()
    private val auth = FirebaseAuth.getInstance()
    
    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList = _pokemonList.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    private val _likedPokemonIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPokemonIds = _likedPokemonIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<PokemonType>(PokemonTypes.All)
    val selectedType = _selectedType.asStateFlow()

    private val _filteredPokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val filteredPokemonList = _filteredPokemonList.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var isLoadingMore = false
    private var hasMoreItems = true
    
    private var showingDirectSearchResult = false
    
    init {
        loadPokemonList()
        observeLikedPokemon()
        observeFilters()
    }

    private fun observeFilters() {
        viewModelScope.launch {
            combine(
                _pokemonList,
                _searchQuery,
                _selectedType
            ) { pokemonList, query, type ->
                val filtered = pokemonList.filter { pokemon ->
                    val matchesSearch = query.isEmpty() || 
                        pokemon.name.contains(query, ignoreCase = true)
                    val matchesType = type == PokemonTypes.All || 
                        pokemon.types.any { it.type.name.equals(type.name, ignoreCase = true) }
                    matchesSearch && matchesType
                }
                // If searching and not found, try to fetch by name
                if (query.isNotEmpty() && filtered.isEmpty()) {
                    try {
                        val details = pokemonRepository.getPokemonDetail(query.lowercase())
                        val newPokemon = Pokemon(
                            id = details.id,
                            name = details.name,
                            url = "https://pokeapi.co/api/v2/pokemon/${details.id}/",
                            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${details.id}.png",
                            types = details.types
                        )
                        showingDirectSearchResult = true
                        return@combine listOf(newPokemon)
                    } catch (e: Exception) {
                        showingDirectSearchResult = false
                        return@combine emptyList()
                    }
                } else if (query.isEmpty() && showingDirectSearchResult) {
                    // Restore full list when search is cleared
                    showingDirectSearchResult = false
                    return@combine pokemonList
                }
                showingDirectSearchResult = false
                filtered
            }.collect { filteredList ->
                _filteredPokemonList.value = filteredList
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedType(type: PokemonType) {
        _selectedType.value = type
    }

    fun loadPokemonList() {
        if (isLoadingMore || !hasMoreItems) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                isLoadingMore = true
                
                val offset = currentPage * pageSize
                val basicList = pokemonRepository.getPokemonList(pageSize, offset)
                
                // Load details in parallel using coroutines
                val detailedList = basicList.map { pokemon ->
                    async(Dispatchers.IO) {
                        try {
                            val details = pokemonRepository.getPokemonDetail(pokemon.name)
                            pokemon.copy(types = details.types)
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Error loading details for ${pokemon.name}", e)
                            pokemon
                        }
                    }
                }.awaitAll()
                
                // Update the list by appending new items
                _pokemonList.value = _pokemonList.value + detailedList
                currentPage++
                hasMoreItems = detailedList.isNotEmpty()
                
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading Pokemon list", e)
                _error.value = e.message ?: "Failed to load Pokémon"
            } finally {
                _isLoading.value = false
                isLoadingMore = false
            }
        }
    }

    fun loadMore() {
        if (!isLoadingMore && hasMoreItems) {
            loadPokemonList()
        }
    }
    
    private fun observeLikedPokemon() {
        viewModelScope.launch {
            likeRepository.observeLikedPokemon()
                .catch { e -> 
                    Log.e("HomeViewModel", "Error observing liked Pokemon", e)
                    _error.value = e.message 
                }
                .collect { likedIds ->
                    _likedPokemonIds.value = likedIds
                }
        }
    }

    fun toggleLikePokemon(pokemonId: String, name: String) {
        viewModelScope.launch {
            try {
                if (auth.currentUser == null) {
                    _error.value = "Please log in to like Pokémon"
                    return@launch
                }
                
                Log.d("HomeViewModel", "Attempting to toggle like for Pokemon: $name")
                likeRepository.toggleLikePokemon(pokemonId, name)
                Log.d("HomeViewModel", "Successfully toggled like for Pokemon: $name")
                
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error toggling Pokemon like", e)
                _error.value = "Failed to toggle like: ${e.message}"
            }
        }
    }
} 