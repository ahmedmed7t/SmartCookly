package com.nexable.smartcookly.feature.recipes.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.data.repository.RecipeRepository
import com.nexable.smartcookly.feature.recipes.presentation.DiscoveryMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscoverRecipesViewModel(
    private val recipeRepository: RecipeRepository,
    private val appPreferences: AppPreferences,
    private val fridgeRepository: FridgeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverRecipesUiState())
    val uiState: StateFlow<DiscoverRecipesUiState> = _uiState.asStateFlow()

    fun loadRecipes(
        discoveryMode: DiscoveryMode,
        cuisines: Set<com.nexable.smartcookly.feature.onboarding.data.model.Cuisine>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                println("DiscoverRecipesViewModel: Starting recipe discovery...")
                println("DiscoverRecipesViewModel: Mode = $discoveryMode, Cuisines = ${cuisines.map { it.displayName }}")
                
                // Load user preferences
                val onboardingData = appPreferences.loadOnboardingData()
                println("DiscoverRecipesViewModel: Loaded preferences - dietary: ${onboardingData.selectedDietaryStyle}, level: ${onboardingData.selectedCookingLevel}")
                
                // Get fridge items
                val fridgeItems = fridgeRepository.getAllItems()
                println("DiscoverRecipesViewModel: Fridge has ${fridgeItems.size} items")
                
                // Call repository
                println("DiscoverRecipesViewModel: Calling OpenAI API...")
                val result = recipeRepository.discoverRecipes(
                    discoveryMode = discoveryMode,
                    cuisines = cuisines,
                    fridgeItems = fridgeItems,
                    dietaryStyle = onboardingData.selectedDietaryStyle,
                    avoidedIngredients = onboardingData.avoidedIngredients,
                    dislikedIngredients = onboardingData.dislikedIngredients,
                    cookingLevel = onboardingData.selectedCookingLevel
                )
                
                println("DiscoverRecipesViewModel: Got API response")
                
                when (result) {
                    is com.nexable.smartcookly.netwrokUtils.Result.Success -> {
                        println("DiscoverRecipesViewModel: Success! Got ${result.data.size} recipes")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            recipes = result.data,
                            error = null
                        )
                    }
                    is com.nexable.smartcookly.netwrokUtils.Result.Error -> {
                        println("DiscoverRecipesViewModel: Error - ${result.error}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = when (result.error) {
                                com.nexable.smartcookly.netwrokUtils.NetworkError.NO_INTERNET -> 
                                    "No internet connection"
                                com.nexable.smartcookly.netwrokUtils.NetworkError.SERVER_ERROR -> 
                                    "Server error. Please try again."
                                com.nexable.smartcookly.netwrokUtils.NetworkError.REQUEST_TIMEOUT ->
                                    "Request timed out. Please try again."
                                else -> "Failed to load recipes: ${result.error}"
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                println("DiscoverRecipesViewModel: Exception - ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "An error occurred: ${e.message}"
                )
            }
        }
    }
    
    fun selectRecipe(recipe: Recipe) {
        _uiState.value = _uiState.value.copy(selectedRecipe = recipe)
    }
    
    fun clearSelectedRecipe() {
        _uiState.value = _uiState.value.copy(selectedRecipe = null)
    }
    
    fun retry() {
        // Retry will be handled by calling loadRecipes again from the screen
        _uiState.value = _uiState.value.copy(error = null)
    }
}
