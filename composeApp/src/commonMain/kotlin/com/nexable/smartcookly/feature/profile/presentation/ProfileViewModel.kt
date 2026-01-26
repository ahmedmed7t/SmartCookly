package com.nexable.smartcookly.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.user.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Load display name from Firebase Auth
            val currentUser = authRepository.getCurrentUser()
            val displayName = currentUser?.displayName ?: ""
            
            // Load preferences from local cache
            val onboardingData = appPreferences.loadOnboardingData()
            
            _uiState.value = ProfileUiState(
                displayName = displayName,
                cuisines = onboardingData.selectedCuisines.map { it.displayName },
                otherCuisineText = onboardingData.otherCuisineText,
                dietaryStyle = onboardingData.selectedDietaryStyle?.displayName,
                otherDietaryStyleText = onboardingData.otherDietaryStyleText,
                avoidedIngredients = onboardingData.avoidedIngredients.map { it.displayName },
                otherIngredientText = onboardingData.otherIngredientText,
                dislikedIngredients = onboardingData.dislikedIngredients.map { it.displayName },
                otherDislikedIngredientText = onboardingData.otherDislikedIngredientText,
                diseases = onboardingData.selectedDiseases.map { it.displayName },
                otherDiseaseText = onboardingData.otherDiseaseText,
                cookingLevel = onboardingData.selectedCookingLevel?.displayName,
                isLoading = false
            )
        }
    }
    
    fun updateDisplayName(newName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            
            val result = authRepository.updateProfile(newName)
            result.fold(
                onSuccess = {
                    // Also update Firestore if user document exists
                    val userId = authRepository.getCurrentUser()?.uid
                    if (userId != null) {
                        try {
                            val profile = appPreferences.toUserProfile()
                            userRepository.saveUserProfile(userId, profile)
                        } catch (e: Exception) {
                            // Log but don't fail - name update succeeded
                            println("Failed to sync profile to Firestore: ${e.message}")
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        displayName = newName,
                        isSaving = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Failed to update name"
                    )
                }
            )
        }
    }
    
    fun refreshProfile() {
        loadProfile()
    }
}
