package com.nexable.smartcookly.feature.recipes.presentation.discover

import com.nexable.smartcookly.feature.recipes.data.model.Recipe

data class DiscoverRecipesUiState(
    val isLoading: Boolean = true,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null,
    val selectedRecipe: Recipe? = null
)
