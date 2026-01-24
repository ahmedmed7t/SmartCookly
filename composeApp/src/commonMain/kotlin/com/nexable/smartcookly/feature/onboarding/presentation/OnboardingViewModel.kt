package com.nexable.smartcookly.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.onboarding.data.OnboardingDataCache
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    init {
        // Load cached data if available
        _uiState.value = _uiState.value.copy(
            selectedCuisines = OnboardingDataCache.selectedCuisines.toSet(),
            otherCuisineText = OnboardingDataCache.otherCuisineText ?: "",
            selectedDietaryStyle = OnboardingDataCache.selectedDietaryStyle,
            otherDietaryStyleText = OnboardingDataCache.otherDietaryStyleText ?: ""
        )
    }
    
    fun toggleCuisineSelection(cuisine: Cuisine) {
        val currentSelected = _uiState.value.selectedCuisines.toMutableSet()
        
        if (cuisine == Cuisine.OTHER) {
            // Toggle other text field visibility
            val showOther = !_uiState.value.showOtherTextField
            _uiState.value = _uiState.value.copy(
                showOtherTextField = showOther,
                selectedCuisines = if (showOther) {
                    currentSelected.apply { add(cuisine) }
                } else {
                    currentSelected.apply { remove(cuisine) }
                }
            )
        } else {
            if (currentSelected.contains(cuisine)) {
                currentSelected.remove(cuisine)
            } else {
                currentSelected.add(cuisine)
            }
            _uiState.value = _uiState.value.copy(selectedCuisines = currentSelected)
        }
        
        // Update cache
        OnboardingDataCache.selectedCuisines = currentSelected
    }
    
    fun updateOtherCuisineText(text: String) {
        _uiState.value = _uiState.value.copy(otherCuisineText = text)
        OnboardingDataCache.otherCuisineText = text.ifBlank { null }
    }
    
    fun goToNextStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep < _uiState.value.totalSteps) {
            _uiState.value = _uiState.value.copy(currentStep = currentStep + 1)
        }
    }
    
    fun goToPreviousStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = currentStep - 1)
        }
    }
    
    fun selectDietaryStyle(style: DietaryStyle) {
        if (style == DietaryStyle.OTHER) {
            // Toggle other text field visibility
            val showOther = !_uiState.value.showOtherDietaryTextField
            _uiState.value = _uiState.value.copy(
                selectedDietaryStyle = if (showOther) style else null,
                showOtherDietaryTextField = showOther
            )
        } else {
            _uiState.value = _uiState.value.copy(
                selectedDietaryStyle = style,
                showOtherDietaryTextField = false
            )
        }
        
        // Update cache
        OnboardingDataCache.selectedDietaryStyle = _uiState.value.selectedDietaryStyle
    }
    
    fun updateOtherDietaryStyleText(text: String) {
        _uiState.value = _uiState.value.copy(otherDietaryStyleText = text)
        OnboardingDataCache.otherDietaryStyleText = text.ifBlank { null }
    }
    
    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Save final data to cache (will be sent to Firestore later)
            OnboardingDataCache.selectedCuisines = _uiState.value.selectedCuisines.toMutableSet()
            OnboardingDataCache.otherCuisineText = _uiState.value.otherCuisineText.ifBlank { null }
            OnboardingDataCache.selectedDietaryStyle = _uiState.value.selectedDietaryStyle
            OnboardingDataCache.otherDietaryStyleText = _uiState.value.otherDietaryStyleText.ifBlank { null }
            onComplete()
        }
    }
}
