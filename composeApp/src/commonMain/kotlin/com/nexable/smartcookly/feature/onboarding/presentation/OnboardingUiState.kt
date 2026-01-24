package com.nexable.smartcookly.feature.onboarding.presentation

import com.nexable.smartcookly.feature.onboarding.data.model.CookingLevel
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.data.model.Disease
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient

data class OnboardingUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 6,
    val selectedCuisines: Set<Cuisine> = emptySet(),
    val otherCuisineText: String = "",
    val showOtherTextField: Boolean = false,
    val selectedDietaryStyle: DietaryStyle? = null,
    val otherDietaryStyleText: String = "",
    val showOtherDietaryTextField: Boolean = false,
    val avoidedIngredients: Set<Ingredient> = emptySet(),
    val otherIngredientText: String = "",
    val showOtherIngredientTextField: Boolean = false,
    val dislikedIngredients: Set<DislikedIngredient> = emptySet(),
    val otherDislikedIngredientText: String = "",
    val showOtherDislikedIngredientTextField: Boolean = false,
    val selectedDiseases: Set<Disease> = emptySet(),
    val otherDiseaseText: String = "",
    val showOtherDiseaseTextField: Boolean = false,
    val selectedCookingLevel: CookingLevel? = null,
    val searchQuery: String = ""
)
