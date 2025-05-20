package com.example.pokedex.network

import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.content.Context
import java.io.File

object RetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB cache
    private var cache: Cache? = null

    fun initialize(context: Context) {
        cache = Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                var request = chain.request()
                request = request.newBuilder()
                    .header("Cache-Control", "public, max-age=86400") // Cache for 24 hours
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val pokeApi: PokeApi by lazy {
        retrofit.create(PokeApi::class.java)
    }
} 