package com.example.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.model.AuthState
import com.example.pokedex.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            _authState.value = AuthState.Success(currentUser.email ?: "")
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val user = repository.signIn(email, password)
                _authState.value = AuthState.Success(user?.email ?: "")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val user = repository.signUp(email, password)
                _authState.value = AuthState.Success(user?.email ?: "")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Initial
    }

    fun getCurrentUserEmail(): String? {
        return repository.getCurrentUser()?.email
    }

    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUser() != null
    }
} 