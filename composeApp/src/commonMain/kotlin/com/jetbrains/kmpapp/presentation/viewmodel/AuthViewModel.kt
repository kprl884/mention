package com.jetbrains.kmpapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

sealed class AuthEvent {
    object LoginWithTwitter : AuthEvent()
    object Logout : AuthEvent()
}

class AuthViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.LoginWithTwitter -> {
                initiateTwitterLogin()
            }
            is AuthEvent.Logout -> {
                logout()
            }
        }
    }
    
    private fun initiateTwitterLogin() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                // TODO: Implement actual Twitter OAuth 2.0 flow
                // For now, simulate the login process
                kotlinx.coroutines.delay(2000) // Simulate network delay
                
                // Simulate successful login
                _state.value = _state.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
                
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Giriş işlemi başarısız oldu"
                )
            }
        }
    }
    
    private fun logout() {
        _state.value = AuthState()
    }
} 