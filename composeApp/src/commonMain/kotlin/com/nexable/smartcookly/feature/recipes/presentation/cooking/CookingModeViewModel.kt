package com.nexable.smartcookly.feature.recipes.presentation.cooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.favorites.data.repository.FavoritesRepository
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.data.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CookingModeViewModel(
    private val recipeRepository: RecipeRepository,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    companion object {
        // Static cache that persists across ViewModel instances
        private var cachedSteps: Map<String, List<com.nexable.smartcookly.feature.recipes.data.model.CookingStep>> = emptyMap()
        private var cachedRecipeKey: String? = null
    }
    
    private val _uiState = MutableStateFlow(CookingModeUiState())
    val uiState: StateFlow<CookingModeUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    
    fun loadCookingSteps(
        recipeName: String, 
        ingredients: List<String>,
        preLoadedSteps: List<com.nexable.smartcookly.feature.recipes.data.model.CookingStep> = emptyList()
    ) {
        // Create a cache key from recipe name and ingredients
        val recipeKey = "$recipeName|${ingredients.joinToString(",")}"
        
        // If we have pre-loaded steps (e.g., from favorites), use them directly
        if (preLoadedSteps.isNotEmpty()) {
            // Cache them for consistency
            cachedRecipeKey = recipeKey
            cachedSteps = cachedSteps + (recipeKey to preLoadedSteps)
            
            _uiState.update { 
                it.copy(
                    recipeName = recipeName,
                    ingredients = ingredients,
                    steps = preLoadedSteps,
                    isLoading = false,
                    error = null,
                    currentStepIndex = 0
                )
            }
            return
        }
        
        // Check if we already have steps cached for this recipe
        val cachedStepsForRecipe = cachedSteps[recipeKey]
        if (cachedRecipeKey == recipeKey && cachedStepsForRecipe != null && cachedStepsForRecipe.isNotEmpty()) {
            // Use cached steps
            _uiState.update { 
                it.copy(
                    recipeName = recipeName,
                    ingredients = ingredients,
                    steps = cachedStepsForRecipe,
                    isLoading = false,
                    error = null,
                    currentStepIndex = 0
                )
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, recipeName = recipeName, ingredients = ingredients) }
            
            val result = recipeRepository.getCookingSteps(recipeName, ingredients)
            
            when (result) {
                is com.nexable.smartcookly.netwrokUtils.Result.Success -> {
                    // Cache the steps
                    cachedRecipeKey = recipeKey
                    cachedSteps = cachedSteps + (recipeKey to result.data)
                    
                    _uiState.update {
                        it.copy(
                            steps = result.data,
                            isLoading = false,
                            currentStepIndex = 0
                        )
                    }
                }
                is com.nexable.smartcookly.netwrokUtils.Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = when (result.error) {
                                com.nexable.smartcookly.netwrokUtils.NetworkError.NO_INTERNET -> 
                                    "No internet connection"
                                com.nexable.smartcookly.netwrokUtils.NetworkError.SERVER_ERROR -> 
                                    "Server error. Please try again."
                                com.nexable.smartcookly.netwrokUtils.NetworkError.REQUEST_TIMEOUT ->
                                    "Request timed out. Please try again."
                                else -> "Failed to load cooking steps: ${result.error}"
                            }
                        )
                    }
                }
            }
        }
    }
    
    fun nextStep() {
        val currentIndex = _uiState.value.currentStepIndex
        val totalSteps = _uiState.value.steps.size
        
        if (currentIndex < totalSteps - 1) {
            resetTimer() // Reset timer when moving to next step
            _uiState.update { it.copy(currentStepIndex = currentIndex + 1) }
        }
    }
    
    fun previousStep() {
        val currentIndex = _uiState.value.currentStepIndex
        
        if (currentIndex > 0) {
            resetTimer() // Reset timer when moving to previous step
            _uiState.update { it.copy(currentStepIndex = currentIndex - 1) }
        }
    }
    
    fun startTimer(minutes: Int) {
        timerJob?.cancel()
        
        val totalSeconds = minutes * 60
        _uiState.update {
            it.copy(
                timerSeconds = totalSeconds,
                timerRunning = true,
                timerFinished = false
            )
        }
        
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0 && _uiState.value.timerRunning) {
                delay(1000)
                _uiState.update { state ->
                    state.copy(timerSeconds = state.timerSeconds - 1)
                }
            }
            
            // Timer finished
            if (_uiState.value.timerSeconds == 0) {
                _uiState.update {
                    it.copy(timerFinished = true, timerRunning = false)
                }
            }
        }
    }
    
    fun pauseTimer() {
        _uiState.update { it.copy(timerRunning = false) }
    }
    
    fun resumeTimer() {
        val currentSeconds = _uiState.value.timerSeconds
        if (currentSeconds > 0 && !_uiState.value.timerRunning) {
            _uiState.update { it.copy(timerRunning = true) }
            
            timerJob = viewModelScope.launch {
                while (_uiState.value.timerSeconds > 0 && _uiState.value.timerRunning) {
                    delay(1000)
                    _uiState.update { state ->
                        state.copy(timerSeconds = state.timerSeconds - 1)
                    }
                }
                
                if (_uiState.value.timerSeconds == 0) {
                    _uiState.update {
                        it.copy(timerFinished = true, timerRunning = false)
                    }
                }
            }
        }
    }
    
    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerSeconds = 0,
                timerRunning = false,
                timerFinished = false
            )
        }
    }
    
    fun finishCooking() {
        _uiState.update { it.copy(isCookingComplete = true) }
    }
    
    fun clearCache() {
        cachedRecipeKey = null
        cachedSteps = emptyMap()
        _uiState.update { 
            it.copy(
                steps = emptyList(),
                currentStepIndex = 0,
                timerSeconds = 0,
                timerRunning = false,
                timerFinished = false,
                isCookingComplete = false
            )
        }
    }
    
    fun addToFavorites(recipe: Recipe) {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            _uiState.update {
                it.copy(favoriteError = "User not authenticated")
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAddingToFavorites = true,
                    favoriteError = null,
                    favoriteAdded = false
                )
            }
            
            try {
                // Merge the loaded cooking steps into the recipe
                val recipeWithSteps = recipe.copy(cookingSteps = _uiState.value.steps)
                favoritesRepository.addToFavorites(userId, recipeWithSteps)
                _uiState.update {
                    it.copy(
                        isAddingToFavorites = false,
                        favoriteAdded = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAddingToFavorites = false,
                        favoriteError = "Failed to add recipe to favorites: ${e.message}"
                    )
                }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
