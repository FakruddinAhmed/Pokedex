package com.example.pokedex.model

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object NotAuthenticated : AuthState()
}

data class AuthCredentials(
    val email: String = "",
    val password: String = "",
    val isValid: Boolean = false
) {
    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        
        fun validateEmail(email: String): Boolean = 
            email.matches(EMAIL_REGEX)
        
        fun validatePassword(password: String): Boolean = 
            password.length >= MIN_PASSWORD_LENGTH
    }
} 