package com.example.pokedex.repository

import android.util.Log
import com.example.pokedex.model.LikedPokemon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LikeRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "LikeRepository"

    // In-memory cache for liked Pokemon IDs
    private val likedPokemonCache = MutableStateFlow<Set<String>>(emptySet())

    fun observeLikedPokemon(): Flow<Set<String>> = callbackFlow {
        val userId = auth.currentUser?.uid
        Log.d(TAG, "Observing liked Pokemon for user: $userId")
        
        if (userId == null) {
            Log.d(TAG, "No user logged in")
            trySend(emptySet())
            return@callbackFlow
        }

        // First, emit cached data if available
        if (likedPokemonCache.value.isNotEmpty()) {
            trySend(likedPokemonCache.value)
        }

        val listenerRegistration = firestore.collection("users")
            .document(userId)
            .collection("likedPokemon")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing liked Pokemon", error)
                    return@addSnapshotListener
                }

                val likedIds = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                Log.d(TAG, "Received liked Pokemon IDs: $likedIds")
                likedPokemonCache.value = likedIds // Update cache
                trySend(likedIds)
            }

        awaitClose { 
            listenerRegistration.remove()
        }
    }

    suspend fun isPokemonLiked(pokemonId: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false

        // Check cache first
        if (likedPokemonCache.value.contains(pokemonId)) {
            return true
        }

        return try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("likedPokemon")
                .document(pokemonId)
            
            // Try cache first, then server
            val snapshot = docRef.get(Source.CACHE).await() 
                ?: docRef.get(Source.SERVER).await()
            
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("LikeRepository", "Error checking if Pokemon is liked", e)
            false
        }
    }

    suspend fun toggleLikePokemon(pokemonId: String, name: String) {
        val userId = auth.currentUser?.uid
        Log.d(TAG, "Attempting to toggle like for Pokemon: $name (ID: $pokemonId)")
        Log.d(TAG, "Current user ID: $userId")

        if (userId == null) {
            Log.e(TAG, "No user logged in")
            throw Exception("User not logged in")
        }

        try {
            val userDoc = firestore.collection("users").document(userId)
            
            // First, ensure the user document exists
            if (!userDoc.get().await().exists()) {
                Log.d(TAG, "Creating user document for $userId")
                userDoc.set(mapOf("email" to auth.currentUser?.email)).await()
            }

            val pokemonDoc = userDoc.collection("likedPokemon").document(pokemonId)
            val exists = pokemonDoc.get().await().exists()
            
            Log.d(TAG, "Pokemon document exists: $exists")

            if (exists) {
                Log.d(TAG, "Attempting to unlike Pokemon: $name")
                pokemonDoc.delete().await()
                // Update cache
                likedPokemonCache.value = likedPokemonCache.value - pokemonId
                Log.d(TAG, "Successfully unliked Pokemon: $name")
            } else {
                Log.d(TAG, "Attempting to like Pokemon: $name")
                val data = hashMapOf(
                    "name" to name,
                    "timestamp" to com.google.firebase.Timestamp.now()
                )
                pokemonDoc.set(data).await()
                // Update cache
                likedPokemonCache.value = likedPokemonCache.value + pokemonId
                Log.d(TAG, "Successfully liked Pokemon: $name")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling Pokemon like", e)
            throw Exception("Failed to toggle like: ${e.message}")
        }
    }

    fun clearCache() {
        likedPokemonCache.value = emptySet()
    }
} 