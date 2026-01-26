package com.nexable.smartcookly.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.data.GoogleSignInProvider
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.user.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
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
                onSuccess = { user ->
                    syncUserData(user?.uid)
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
    
    fun signInWithGoogle(googleSignInProvider: GoogleSignInProvider) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGoogleLoading = true,
                error = null
            )
            
            val googleCredentialResult = googleSignInProvider.signIn()
            
            googleCredentialResult.fold(
                onSuccess = { googleCredential ->
                    val result = authRepository.signInWithGoogle(googleCredential)
                    
                    result.fold(
                        onSuccess = { user ->
                            syncUserData(user?.uid)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isGoogleLoading = false,
                                isLoginSuccess = true,
                                error = null
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isGoogleLoading = false,
                                error = getErrorMessage(exception)
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isGoogleLoading = false,
                        error = getErrorMessage(exception)
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private suspend fun syncUserData(userId: String?) {
        if (userId == null) return
        
        try {
            val userExists = userRepository.userExists(userId)
            
            if (userExists) {
                // Existing user: pull data from Firestore and update local cache
                val profile = userRepository.getUserProfile(userId)
                profile?.let {
                    appPreferences.updateFromUserProfile(it)
                }
            } else {
                // New user: push local onboarding data to Firestore
                val localProfile = appPreferences.toUserProfile()
                println("Firestore: Saving new user profile for userId: $userId")
                userRepository.saveUserProfile(userId, localProfile)
                println("Firestore: Successfully saved user profile")
            }
        } catch (e: Exception) {
            println("Firestore sync error: ${e.message}")
            e.printStackTrace()
        }
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
