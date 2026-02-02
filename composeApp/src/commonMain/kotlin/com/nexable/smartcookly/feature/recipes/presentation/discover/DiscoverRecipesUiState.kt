package com.nexable.smartcookly.feature.recipes.presentation.discover

import com.nexable.smartcookly.feature.recipes.data.model.Recipe

data class DiscoverRecipesUiState(
    val isLoading: Boolean = true,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null,
    val selectedRecipe: Recipe? = null,
    // Favorite states
    val isAddingFavorite: Boolean = false,
    val addingFavoriteRecipeId: String? = null, // Track which recipe is being added
    val favoriteAddedRecipeId: String? = null,
    val favoriteError: String? = null,
    val favoritedRecipeIds: Set<String> = emptySet() // Cache of favorited recipe IDs
)
