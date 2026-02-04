package com.nexable.smartcookly.feature.recipes.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.favorites.data.repository.FavoritesRepository
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.data.repository.RecipeRepository
import com.nexable.smartcookly.feature.recipes.presentation.DiscoveryMode
import com.nexable.smartcookly.navigation.QuickMealsCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoverRecipesViewModel(
    private val recipeRepository: RecipeRepository,
    private val appPreferences: AppPreferences,
    private val fridgeRepository: FridgeRepository,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository
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
                
                // Check cache for QUICK_MEALS mode
                if (discoveryMode == DiscoveryMode.QUICK_MEALS) {
                    val cachedRecipes = QuickMealsCache.getRecipes()
                    if (cachedRecipes != null && cachedRecipes.isNotEmpty()) {
                        println("DiscoverRecipesViewModel: Using cached quick meals recipes (${cachedRecipes.size} recipes)")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            recipes = cachedRecipes,
                            error = null
                        )
                        return@launch
                    }
                }
                
                // Load user preferences
                val onboardingData = appPreferences.loadOnboardingData()
                println("DiscoverRecipesViewModel: Loaded preferences - dietary: ${onboardingData.selectedDietaryStyle}, level: ${onboardingData.selectedCookingLevel}")
                
                // Get fridge items (not used for QUICK_MEALS mode)
                val fridgeItems = if (discoveryMode == DiscoveryMode.QUICK_MEALS) {
                    emptyList() // Don't use fridge items for quick meals
                } else {
                    fridgeRepository.getAllItems()
                }
                println("DiscoverRecipesViewModel: Fridge has ${fridgeItems.size} items")
                
                // Call repository
                println("DiscoverRecipesViewModel: Calling OpenAI API...")
                val result = recipeRepository.discoverRecipes(
                    discoveryMode = discoveryMode,
                    cuisines = cuisines,
                    fridgeItems = fridgeItems,
                    dietaryStyle = if (discoveryMode == DiscoveryMode.QUICK_MEALS) null else onboardingData.selectedDietaryStyle,
                    avoidedIngredients = if (discoveryMode == DiscoveryMode.QUICK_MEALS) emptySet() else onboardingData.avoidedIngredients,
                    dislikedIngredients = if (discoveryMode == DiscoveryMode.QUICK_MEALS) emptySet() else onboardingData.dislikedIngredients,
                    cookingLevel = if (discoveryMode == DiscoveryMode.QUICK_MEALS) null else onboardingData.selectedCookingLevel
                )
                
                println("DiscoverRecipesViewModel: Got API response")
                
                when (result) {
                    is com.nexable.smartcookly.netwrokUtils.Result.Success -> {
                        println("DiscoverRecipesViewModel: Success! Got ${result.data.size} recipes")
                        
                        // Cache recipes for QUICK_MEALS mode
                        if (discoveryMode == DiscoveryMode.QUICK_MEALS) {
                            QuickMealsCache.storeRecipes(result.data)
                            println("DiscoverRecipesViewModel: Cached quick meals recipes")
                        }
                        
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
                    isAddingFavorite = true,
                    addingFavoriteRecipeId = recipe.id,
                    favoriteError = null,
                    favoriteAddedRecipeId = null
                )
            }
            
            try {
                // First, fetch cooking steps
                val stepsResult = recipeRepository.getCookingSteps(recipe.name, recipe.ingredients)
                
                when (stepsResult) {
                    is com.nexable.smartcookly.netwrokUtils.Result.Success -> {
                        // Merge cooking steps into recipe
                        val recipeWithSteps = recipe.copy(cookingSteps = stepsResult.data)
                        
                        // Save to favorites
                        favoritesRepository.addToFavorites(userId, recipeWithSteps)
                        
                        _uiState.update {
                            it.copy(
                                isAddingFavorite = false,
                                addingFavoriteRecipeId = null,
                                favoriteAddedRecipeId = recipe.id,
                                favoritedRecipeIds = it.favoritedRecipeIds + recipe.id
                            )
                        }
                    }
                    is com.nexable.smartcookly.netwrokUtils.Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isAddingFavorite = false,
                                addingFavoriteRecipeId = null,
                                favoriteError = when (stepsResult.error) {
                                    com.nexable.smartcookly.netwrokUtils.NetworkError.NO_INTERNET -> 
                                        "No internet connection"
                                    com.nexable.smartcookly.netwrokUtils.NetworkError.SERVER_ERROR -> 
                                        "Server error. Please try again."
                                    com.nexable.smartcookly.netwrokUtils.NetworkError.REQUEST_TIMEOUT ->
                                        "Request timed out. Please try again."
                                    else -> "Failed to fetch cooking steps: ${stepsResult.error}"
                                }
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAddingFavorite = false,
                        addingFavoriteRecipeId = null,
                        favoriteError = "Failed to add recipe to favorites: ${e.message}"
                    )
                }
            }
        }
    }
}
