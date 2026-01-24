package com.nexable.smartcookly.feature.onboarding.presentation

import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle

data class OnboardingUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val selectedCuisines: Set<Cuisine> = emptySet(),
    val otherCuisineText: String = "",
    val showOtherTextField: Boolean = false,
    val selectedDietaryStyle: DietaryStyle? = null,
    val otherDietaryStyleText: String = "",
    val showOtherDietaryTextField: Boolean = false
)
