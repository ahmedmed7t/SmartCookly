package com.nexable.smartcookly.feature.auth.presentation.signup

data class SignUpUiState(
    // Form fields
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    
    // Field errors (null = no error)
    val firstNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    
    // General state
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val isSignUpSuccess: Boolean = false
)
