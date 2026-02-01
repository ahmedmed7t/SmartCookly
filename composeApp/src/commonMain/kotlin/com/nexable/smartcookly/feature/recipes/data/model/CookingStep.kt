package com.nexable.smartcookly.feature.recipes.data.model

data class CookingStep(
    val stepNumber: Int,
    val description: String,
    val ingredientsUsed: List<String>,
    val timeMinutes: Int
)
