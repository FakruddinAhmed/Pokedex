package com.example.pokedex

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.example.pokedex.network.RetrofitClient

class PokedexApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        
        // Initialize RetrofitClient with cache
        RetrofitClient.initialize(this)
        
        // Configure Firestore for offline persistence
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = settings
            // Enable offline persistence
            enableNetwork()
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of app memory for image cache
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(512L * 1024 * 1024) // 512MB cache
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false) // Cache images even if response says not to
            .build()
    }

    private fun initializeFirebase() {
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            
            // Configure Firestore settings
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)  // Enable offline persistence
                .build()
            firestore.firestoreSettings = settings
            
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
        }
    }

    companion object {
        private const val TAG = "PokedexApplication"
    }
} 