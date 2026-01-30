package com.nexable.smartcookly.feature.recipes.presentation

import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine

data class RecipesUiState(
    val discoveryMode: DiscoveryMode = DiscoveryMode.PREFERENCES,
    val cuisineContext: CuisineContext = CuisineContext.FAVORITES,
    val favoriteCuisines: Set<Cuisine> = emptySet(),
    val selectedOtherCuisines: Set<Cuisine> = emptySet(),
    val isBottomSheetVisible: Boolean = false,
    val isLoading: Boolean = false,
    val fridgeItemsCount: Int = 0
)

enum class DiscoveryMode {
    PREFERENCES,
    FRIDGE,
    BOTH
}

enum class CuisineContext {
    FAVORITES,
    SELECT_OTHERS
}
