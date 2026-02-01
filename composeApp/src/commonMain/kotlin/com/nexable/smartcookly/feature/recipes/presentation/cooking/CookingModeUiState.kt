package com.nexable.smartcookly.feature.recipes.presentation.cooking

import com.nexable.smartcookly.feature.recipes.data.model.CookingStep

data class CookingModeUiState(
    val recipeName: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<CookingStep> = emptyList(),
    val currentStepIndex: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    // Timer state
    val timerSeconds: Int = 0,
    val timerRunning: Boolean = false,
    val timerFinished: Boolean = false,
    // Completion state
    val isCookingComplete: Boolean = false
) {
    val currentStep: CookingStep?
        get() = steps.getOrNull(currentStepIndex)
    
    val isFirstStep: Boolean
        get() = currentStepIndex == 0
    
    val isLastStep: Boolean
        get() = currentStepIndex == steps.size - 1
    
    val progress: Float
        get() = if (steps.isEmpty()) 0f else (currentStepIndex + 1).toFloat() / steps.size.toFloat()
}
