package com.nexable.smartcookly.feature.favorites.presentation

import com.nexable.smartcookly.feature.recipes.data.model.Recipe

data class FavoritesUiState(
    val favorites: List<Recipe> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isRemoving: String? = null // recipeId being removed
) {
    val isEmpty: Boolean
        get() = favorites.isEmpty() && !isLoading && error == null
}
