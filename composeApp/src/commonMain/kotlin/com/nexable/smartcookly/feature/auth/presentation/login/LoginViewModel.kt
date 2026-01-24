package com.nexable.smartcookly.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            error = null
        )
    }
    
    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            error = null
        )
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            passwordVisible = !_uiState.value.passwordVisible
        )
    }
    
    fun signIn() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            val state = _uiState.value
            val result = authRepository.signIn(state.email, state.password)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = getErrorMessage(exception)
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun getErrorMessage(exception: Throwable): String {
        val message = exception.message ?: "An error occurred"
        
        return when {
            message.contains("user-not-found", ignoreCase = true) -> 
                "No account found with this email. Please sign up first."
            message.contains("wrong-password", ignoreCase = true) -> 
                "Incorrect password. Please try again."
            message.contains("invalid-email", ignoreCase = true) -> 
                "Please enter a valid email address."
            message.contains("network", ignoreCase = true) -> 
                "Network error. Please check your internet connection."
            message.contains("too-many-requests", ignoreCase = true) -> 
                "Too many failed attempts. Please try again later."
            else -> message
        }
    }
}
