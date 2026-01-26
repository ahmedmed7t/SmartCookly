package com.nexable.smartcookly.feature.auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.user.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    
    fun updateFirstName(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            firstNameError = null
        )
    }
    
    fun updateLastName(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value
        )
    }
    
    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            emailError = null
        )
    }
    
    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null
        )
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            passwordVisible = !_uiState.value.passwordVisible
        )
    }
    
    fun signUp() {
        viewModelScope.launch {
            // Clear previous errors
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                firstNameError = null,
                emailError = null,
                passwordError = null,
                generalError = null
            )
            
            // Validate fields
            val validationResult = validateFields()
            if (!validationResult.isValid) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    firstNameError = validationResult.firstNameError,
                    emailError = validationResult.emailError,
                    passwordError = validationResult.passwordError
                )
                return@launch
            }
            
            val state = _uiState.value
            val result = authRepository.signUp(state.email, state.password)
            
            result.fold(
                onSuccess = { user ->
                    // Update profile with display name
                    val displayName = if (state.lastName.isNotBlank()) {
                        "${state.firstName} ${state.lastName}"
                    } else {
                        state.firstName
                    }
                    
                    authRepository.updateProfile(displayName).fold(
                        onSuccess = {
                            syncUserData(user?.uid)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSignUpSuccess = true,
                                generalError = null
                            )
                        },
                        onFailure = { error ->
                            // Profile update failed, but user is still created
                            syncUserData(user?.uid)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSignUpSuccess = true,
                                generalError = null
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = getErrorMessage(exception)
                    )
                }
            )
        }
    }
    
    private fun validateFields(): ValidationResult {
        val state = _uiState.value
        var firstNameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        
        // Validate first name
        if (state.firstName.isBlank()) {
            firstNameError = "First name is required"
        }
        
        // Validate email
        if (state.email.isBlank()) {
            emailError = "Email is required"
        } else if (!isValidEmail(state.email)) {
            emailError = "Please enter a valid email"
        }
        
        // Validate password
        if (state.password.isBlank()) {
            passwordError = "Password is required"
        } else if (state.password.length <= 6) {
            passwordError = "Password must be more than 6 characters"
        }
        
        return ValidationResult(
            isValid = firstNameError == null && emailError == null && passwordError == null,
            firstNameError = firstNameError,
            emailError = emailError,
            passwordError = passwordError
        )
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return emailRegex.toRegex().matches(email)
    }
    
    private suspend fun syncUserData(userId: String?) {
        if (userId == null) return
        
        try {
            // New user: push local onboarding data to Firestore
            val localProfile = appPreferences.toUserProfile()
            println("Firestore: Saving new user profile for userId: $userId")
            userRepository.saveUserProfile(userId, localProfile)
            println("Firestore: Successfully saved user profile")
        } catch (e: Exception) {
            println("Firestore sync error: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun getErrorMessage(exception: Throwable): String {
        val message = exception.message ?: "An error occurred"
        
        return when {
            message.contains("email-already-in-use", ignoreCase = true) -> 
                "This email is already registered. Please sign in instead."
            message.contains("invalid-email", ignoreCase = true) -> 
                "Please enter a valid email address."
            message.contains("weak-password", ignoreCase = true) -> 
                "Password is too weak. Please use at least 6 characters."
            message.contains("network", ignoreCase = true) -> 
                "Network error. Please check your internet connection."
            message.contains("too-many-requests", ignoreCase = true) -> 
                "Too many failed attempts. Please try again later."
            else -> message
        }
    }
    
    private data class ValidationResult(
        val isValid: Boolean,
        val firstNameError: String?,
        val emailError: String?,
        val passwordError: String?
    )
}
