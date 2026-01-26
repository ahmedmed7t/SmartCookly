package com.nexable.smartcookly.feature.profile.presentation

data class ProfileUiState(
    val displayName: String = "",
    val cuisines: List<String> = emptyList(),
    val otherCuisineText: String? = null,
    val dietaryStyle: String? = null,
    val otherDietaryStyleText: String? = null,
    val avoidedIngredients: List<String> = emptyList(),
    val otherIngredientText: String? = null,
    val dislikedIngredients: List<String> = emptyList(),
    val otherDislikedIngredientText: String? = null,
    val diseases: List<String> = emptyList(),
    val otherDiseaseText: String? = null,
    val cookingLevel: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
