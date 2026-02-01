package com.nexable.smartcookly.feature.recipes.data.model

data class Recipe(
    val id: String,
    val name: String,
    val cuisine: String,
    val imageUrl: String,
    val cookingTimeMinutes: Int,
    val ingredients: List<String>,
    val missingIngredients: List<String>,
    val fitPercentage: Int, // 0-100
    val rating: Float,
    val description: String = "",
    val cookingSteps: List<CookingStep> = emptyList()
)
