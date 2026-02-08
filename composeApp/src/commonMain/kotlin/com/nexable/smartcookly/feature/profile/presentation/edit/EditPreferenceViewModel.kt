package com.nexable.smartcookly.feature.profile.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.onboarding.data.model.*
import com.nexable.smartcookly.feature.user.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPreferenceViewModel(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    
    // Cuisines state
    private val _selectedCuisines = MutableStateFlow<Set<Cuisine>>(emptySet())
    val selectedCuisines: StateFlow<Set<Cuisine>> = _selectedCuisines.asStateFlow()
    
    private val _otherCuisineText = MutableStateFlow("")
    val otherCuisineText: StateFlow<String> = _otherCuisineText.asStateFlow()
    
    // Dietary style state
    private val _selectedDietaryStyle = MutableStateFlow<DietaryStyle?>(null)
    val selectedDietaryStyle: StateFlow<DietaryStyle?> = _selectedDietaryStyle.asStateFlow()
    
    private val _otherDietaryStyleText = MutableStateFlow("")
    val otherDietaryStyleText: StateFlow<String> = _otherDietaryStyleText.asStateFlow()
    
    // Avoided ingredients state
    private val _avoidedIngredients = MutableStateFlow<Set<Ingredient>>(emptySet())
    val avoidedIngredients: StateFlow<Set<Ingredient>> = _avoidedIngredients.asStateFlow()
    
    private val _otherIngredientText = MutableStateFlow("")
    val otherIngredientText: StateFlow<String> = _otherIngredientText.asStateFlow()
    
    // Disliked ingredients state
    private val _dislikedIngredients = MutableStateFlow<Set<DislikedIngredient>>(emptySet())
    val dislikedIngredients: StateFlow<Set<DislikedIngredient>> = _dislikedIngredients.asStateFlow()
    
    private val _otherDislikedIngredientText = MutableStateFlow("")
    val otherDislikedIngredientText: StateFlow<String> = _otherDislikedIngredientText.asStateFlow()
    
    // Diseases state
    private val _selectedDiseases = MutableStateFlow<Set<Disease>>(emptySet())
    val selectedDiseases: StateFlow<Set<Disease>> = _selectedDiseases.asStateFlow()
    
    private val _otherDiseaseText = MutableStateFlow("")
    val otherDiseaseText: StateFlow<String> = _otherDiseaseText.asStateFlow()
    
    // Cooking level state
    private val _selectedCookingLevel = MutableStateFlow<CookingLevel?>(null)
    val selectedCookingLevel: StateFlow<CookingLevel?> = _selectedCookingLevel.asStateFlow()
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()
    
    fun loadCurrentPreferences() {
        val onboardingData = appPreferences.loadOnboardingData()
        _selectedCuisines.value = onboardingData.selectedCuisines
        _otherCuisineText.value = onboardingData.otherCuisineText ?: ""
        _selectedDietaryStyle.value = onboardingData.selectedDietaryStyle
        _otherDietaryStyleText.value = onboardingData.otherDietaryStyleText ?: ""
        _avoidedIngredients.value = onboardingData.avoidedIngredients
        _otherIngredientText.value = onboardingData.otherIngredientText ?: ""
        _dislikedIngredients.value = onboardingData.dislikedIngredients
        _otherDislikedIngredientText.value = onboardingData.otherDislikedIngredientText ?: ""
        _selectedDiseases.value = onboardingData.selectedDiseases
        _otherDiseaseText.value = onboardingData.otherDiseaseText ?: ""
        _selectedCookingLevel.value = onboardingData.selectedCookingLevel
    }
    
    fun toggleCuisine(cuisine: Cuisine) {
        _selectedCuisines.value = if (_selectedCuisines.value.contains(cuisine)) {
            _selectedCuisines.value - cuisine
        } else {
            _selectedCuisines.value + cuisine
        }
    }
    
    fun updateOtherCuisineText(text: String) {
        _otherCuisineText.value = text
    }
    
    fun selectDietaryStyle(style: DietaryStyle) {
        _selectedDietaryStyle.value = if (_selectedDietaryStyle.value == style) null else style
    }
    
    fun updateOtherDietaryStyleText(text: String) {
        _otherDietaryStyleText.value = text
    }
    
    fun toggleAvoidedIngredient(ingredient: Ingredient) {
        val currentSelected = _avoidedIngredients.value.toMutableSet()
        
        if (ingredient == Ingredient.NOTHING) {
            // When NOTHING is selected, clear all other selections
            if (currentSelected.contains(Ingredient.NOTHING)) {
                currentSelected.clear()
            } else {
                currentSelected.clear()
                currentSelected.add(Ingredient.NOTHING)
            }
            _avoidedIngredients.value = currentSelected
        } else {
            // When any other ingredient is selected, remove NOTHING if present
            currentSelected.remove(Ingredient.NOTHING)
            if (currentSelected.contains(ingredient)) {
                currentSelected.remove(ingredient)
            } else {
                currentSelected.add(ingredient)
            }
            _avoidedIngredients.value = currentSelected
        }
    }
    
    fun updateOtherIngredientText(text: String) {
        _otherIngredientText.value = text
    }
    
    fun toggleDislikedIngredient(ingredient: DislikedIngredient) {
        val currentSelected = _dislikedIngredients.value.toMutableSet()
        
        if (ingredient == DislikedIngredient.NOTHING) {
            // When NOTHING is selected, clear all other selections
            if (currentSelected.contains(DislikedIngredient.NOTHING)) {
                currentSelected.clear()
            } else {
                currentSelected.clear()
                currentSelected.add(DislikedIngredient.NOTHING)
            }
            _dislikedIngredients.value = currentSelected
        } else {
            // When any other ingredient is selected, remove NOTHING if present
            currentSelected.remove(DislikedIngredient.NOTHING)
            if (currentSelected.contains(ingredient)) {
                currentSelected.remove(ingredient)
            } else {
                currentSelected.add(ingredient)
            }
            _dislikedIngredients.value = currentSelected
        }
    }
    
    fun updateOtherDislikedIngredientText(text: String) {
        _otherDislikedIngredientText.value = text
    }
    
    fun toggleDisease(disease: Disease) {
        val currentSelected = _selectedDiseases.value.toMutableSet()
        
        if (disease == Disease.NOTHING) {
            // When NOTHING is selected, clear all other selections
            if (currentSelected.contains(Disease.NOTHING)) {
                currentSelected.clear()
            } else {
                currentSelected.clear()
                currentSelected.add(Disease.NOTHING)
            }
            _selectedDiseases.value = currentSelected
        } else {
            // When any other disease is selected, remove NOTHING if present
            currentSelected.remove(Disease.NOTHING)
            if (currentSelected.contains(disease)) {
                currentSelected.remove(disease)
            } else {
                currentSelected.add(disease)
            }
            _selectedDiseases.value = currentSelected
        }
    }
    
    fun updateOtherDiseaseText(text: String) {
        _otherDiseaseText.value = text
    }
    
    fun selectCookingLevel(level: CookingLevel) {
        _selectedCookingLevel.value = if (_selectedCookingLevel.value == level) null else level
    }
    
    suspend fun savePreferences(userId: String): Result<Unit> {
        return try {
            _isSaving.value = true
            
            // Save to local cache
            appPreferences.saveOnboardingData(
                currentStep = 1,
                selectedCuisines = _selectedCuisines.value,
                otherCuisineText = _otherCuisineText.value.takeIf { it.isNotEmpty() },
                selectedDietaryStyle = _selectedDietaryStyle.value,
                otherDietaryStyleText = _otherDietaryStyleText.value.takeIf { it.isNotEmpty() },
                avoidedIngredients = _avoidedIngredients.value,
                otherIngredientText = _otherIngredientText.value.takeIf { it.isNotEmpty() },
                dislikedIngredients = _dislikedIngredients.value,
                otherDislikedIngredientText = _otherDislikedIngredientText.value.takeIf { it.isNotEmpty() },
                selectedDiseases = _selectedDiseases.value,
                otherDiseaseText = _otherDiseaseText.value.takeIf { it.isNotEmpty() },
                selectedCookingLevel = _selectedCookingLevel.value
            )
            
            // Save to Firestore
            val profile = appPreferences.toUserProfile()
            userRepository.saveUserProfile(userId, profile)
            
            _isSaving.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isSaving.value = false
            Result.failure(e)
        }
    }
}
