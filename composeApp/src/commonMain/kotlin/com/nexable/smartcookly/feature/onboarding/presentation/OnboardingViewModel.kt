package com.nexable.smartcookly.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.onboarding.data.OnboardingDataCache
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.data.model.Disease
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    init {
        // Load persisted data from AppPreferences
        val savedData = appPreferences.loadOnboardingData()
        _uiState.value = _uiState.value.copy(
            currentStep = savedData.currentStep,
            selectedCuisines = savedData.selectedCuisines,
            otherCuisineText = savedData.otherCuisineText ?: "",
            selectedDietaryStyle = savedData.selectedDietaryStyle,
            otherDietaryStyleText = savedData.otherDietaryStyleText ?: "",
            avoidedIngredients = savedData.avoidedIngredients,
            otherIngredientText = savedData.otherIngredientText ?: "",
            dislikedIngredients = savedData.dislikedIngredients,
            otherDislikedIngredientText = savedData.otherDislikedIngredientText ?: "",
            selectedDiseases = savedData.selectedDiseases,
            otherDiseaseText = savedData.otherDiseaseText ?: ""
        )
        
        // Also sync to cache for temporary state during session
        syncToCache()
    }
    
    private fun syncToCache() {
        OnboardingDataCache.currentStep = _uiState.value.currentStep
        OnboardingDataCache.selectedCuisines = _uiState.value.selectedCuisines.toMutableSet()
        OnboardingDataCache.otherCuisineText = _uiState.value.otherCuisineText.ifBlank { null }
        OnboardingDataCache.selectedDietaryStyle = _uiState.value.selectedDietaryStyle
        OnboardingDataCache.otherDietaryStyleText = _uiState.value.otherDietaryStyleText.ifBlank { null }
        OnboardingDataCache.avoidedIngredients = _uiState.value.avoidedIngredients.toMutableSet()
        OnboardingDataCache.otherIngredientText = _uiState.value.otherIngredientText.ifBlank { null }
        OnboardingDataCache.dislikedIngredients = _uiState.value.dislikedIngredients.toMutableSet()
        OnboardingDataCache.otherDislikedIngredientText = _uiState.value.otherDislikedIngredientText.ifBlank { null }
        OnboardingDataCache.selectedDiseases = _uiState.value.selectedDiseases.toMutableSet()
        OnboardingDataCache.otherDiseaseText = _uiState.value.otherDiseaseText.ifBlank { null }
    }
    
    private fun persistToPreferences() {
        appPreferences.saveOnboardingData(
            currentStep = _uiState.value.currentStep,
            selectedCuisines = _uiState.value.selectedCuisines,
            otherCuisineText = _uiState.value.otherCuisineText.ifBlank { null },
            selectedDietaryStyle = _uiState.value.selectedDietaryStyle,
            otherDietaryStyleText = _uiState.value.otherDietaryStyleText.ifBlank { null },
            avoidedIngredients = _uiState.value.avoidedIngredients,
            otherIngredientText = _uiState.value.otherIngredientText.ifBlank { null },
            dislikedIngredients = _uiState.value.dislikedIngredients,
            otherDislikedIngredientText = _uiState.value.otherDislikedIngredientText.ifBlank { null },
            selectedDiseases = _uiState.value.selectedDiseases,
            otherDiseaseText = _uiState.value.otherDiseaseText.ifBlank { null }
        )
        syncToCache()
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
        
        persistToPreferences()
    }
    
    fun updateOtherCuisineText(text: String) {
        _uiState.value = _uiState.value.copy(otherCuisineText = text)
        persistToPreferences()
    }
    
    fun goToNextStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep < _uiState.value.totalSteps) {
            val newStep = currentStep + 1
            _uiState.value = _uiState.value.copy(currentStep = newStep)
            persistToPreferences()
        }
    }
    
    fun goToPreviousStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep > 1) {
            val newStep = currentStep - 1
            _uiState.value = _uiState.value.copy(currentStep = newStep)
            persistToPreferences()
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
        
        persistToPreferences()
    }
    
    fun updateOtherDietaryStyleText(text: String) {
        _uiState.value = _uiState.value.copy(otherDietaryStyleText = text)
        persistToPreferences()
    }
    
    fun toggleIngredientSelection(ingredient: Ingredient) {
        val currentSelected = _uiState.value.avoidedIngredients.toMutableSet()
        
        if (ingredient == Ingredient.OTHER) {
            // Toggle other text field visibility
            val showOther = !_uiState.value.showOtherIngredientTextField
            _uiState.value = _uiState.value.copy(
                showOtherIngredientTextField = showOther,
                avoidedIngredients = if (showOther) {
                    currentSelected.apply { add(ingredient) }
                } else {
                    currentSelected.apply { remove(ingredient) }
                }
            )
        } else {
            if (currentSelected.contains(ingredient)) {
                currentSelected.remove(ingredient)
            } else {
                currentSelected.add(ingredient)
            }
            _uiState.value = _uiState.value.copy(avoidedIngredients = currentSelected)
        }
        
        persistToPreferences()
    }
    
    fun updateOtherIngredientText(text: String) {
        _uiState.value = _uiState.value.copy(otherIngredientText = text)
        persistToPreferences()
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    
    fun toggleDislikedIngredientSelection(ingredient: DislikedIngredient) {
        val currentSelected = _uiState.value.dislikedIngredients.toMutableSet()
        
        if (ingredient == DislikedIngredient.OTHER) {
            // Toggle other text field visibility
            val showOther = !_uiState.value.showOtherDislikedIngredientTextField
            _uiState.value = _uiState.value.copy(
                showOtherDislikedIngredientTextField = showOther,
                dislikedIngredients = if (showOther) {
                    currentSelected.apply { add(ingredient) }
                } else {
                    currentSelected.apply { remove(ingredient) }
                }
            )
        } else {
            if (currentSelected.contains(ingredient)) {
                currentSelected.remove(ingredient)
            } else {
                currentSelected.add(ingredient)
            }
            _uiState.value = _uiState.value.copy(dislikedIngredients = currentSelected)
        }
        
        persistToPreferences()
    }
    
    fun updateOtherDislikedIngredientText(text: String) {
        _uiState.value = _uiState.value.copy(otherDislikedIngredientText = text)
        persistToPreferences()
    }
    
    fun toggleDiseaseSelection(disease: Disease) {
        val currentSelected = _uiState.value.selectedDiseases.toMutableSet()
        
        if (disease == Disease.OTHER) {
            // Toggle other text field visibility
            val showOther = !_uiState.value.showOtherDiseaseTextField
            _uiState.value = _uiState.value.copy(
                showOtherDiseaseTextField = showOther,
                selectedDiseases = if (showOther) {
                    currentSelected.apply { add(disease) }
                } else {
                    currentSelected.apply { remove(disease) }
                }
            )
        } else {
            if (currentSelected.contains(disease)) {
                currentSelected.remove(disease)
            } else {
                currentSelected.add(disease)
            }
            _uiState.value = _uiState.value.copy(selectedDiseases = currentSelected)
        }
        
        persistToPreferences()
    }
    
    fun updateOtherDiseaseText(text: String) {
        _uiState.value = _uiState.value.copy(otherDiseaseText = text)
        persistToPreferences()
    }
    
    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Final persistence (data is already persisted after each action)
            persistToPreferences()
            // Sync to cache for potential Firestore upload later
            syncToCache()
            onComplete()
        }
    }
}
