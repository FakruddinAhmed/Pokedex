package com.example.pokedex.repository

import android.util.Log
import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonDetails
import com.example.pokedex.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class PokemonRepository {
    private val api = RetrofitClient.pokeApi
    private val mutex = Mutex()
    
    // Cache for Pokemon list pages
    private val pokemonListCache = mutableMapOf<Int, List<Pokemon>>()
    
    // Cache for Pokemon details
    private val pokemonDetailsCache = mutableMapOf<String, PokemonDetails>()

    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): List<Pokemon> = withContext(Dispatchers.IO) {
        val page = offset / limit
        
        // Check cache first
        pokemonListCache[page]?.let { return@withContext it }

        try {
            mutex.withLock {
                // Double-check cache after acquiring lock
                pokemonListCache[page]?.let { return@withLock it }

                val response = api.getPokemonList(limit, offset)
                val pokemonList = response.results.mapIndexed { index, item ->
                    val number = item.url.split("/")
                        .dropLast(1)
                        .last()
                    Pokemon(
                        id = number.toInt(),
                        name = item.name,
                        url = item.url
                    )
                }
                
                // Update cache
                pokemonListCache[page] = pokemonList
                pokemonList
            }
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error fetching Pokemon list", e)
            // Return cached data if available, otherwise throw
            pokemonListCache[page] ?: throw Exception("Failed to load Pokemon list: ${e.message}")
        }
    }

    suspend fun getPokemonDetail(name: String): PokemonDetails = withContext(Dispatchers.IO) {
        Log.d("PokemonRepository", "Getting Pokemon details for: $name")
        // Check cache first
        pokemonDetailsCache[name]?.let {
            Log.d("PokemonRepository", "Found Pokemon details in cache for: $name")
            return@withContext it
        }

        try {
            mutex.withLock {
                // Double-check cache after acquiring lock
                pokemonDetailsCache[name]?.let {
                    Log.d("PokemonRepository", "Found Pokemon details in cache after lock for: $name")
                    return@withLock it
                }

                Log.d("PokemonRepository", "Making API call for Pokemon details: $name")
                val details = api.getPokemonDetail(name.lowercase())
                Log.d("PokemonRepository", "Successfully received Pokemon details from API: ${details.name}")
                Log.d("PokemonRepository", "Stats count: ${details.stats.size}")
                Log.d("PokemonRepository", "Types count: ${details.types.size}")
                Log.d("PokemonRepository", "Abilities count: ${details.abilities.size}")
                
                // Update cache
                pokemonDetailsCache[name] = details
                Log.d("PokemonRepository", "Cached Pokemon details for: $name")
                details
            }
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error fetching Pokemon detail for $name", e)
            // Return cached data if available, otherwise throw
            pokemonDetailsCache[name]?.let {
                Log.d("PokemonRepository", "Returning cached data after error for: $name")
                return@withContext it
            }
            throw Exception("Failed to load Pokemon details: ${e.message}")
        }
    }

    fun clearCache() {
        pokemonListCache.clear()
        pokemonDetailsCache.clear()
    }
} 