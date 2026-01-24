package com.nexable.smartcookly.feature.onboarding.data

import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.data.model.Disease
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient

object OnboardingDataCache {
    var selectedCuisines: MutableSet<Cuisine> = mutableSetOf()
    var otherCuisineText: String? = null
    var selectedDietaryStyle: DietaryStyle? = null
    var otherDietaryStyleText: String? = null
    var avoidedIngredients: MutableSet<Ingredient> = mutableSetOf()
    var otherIngredientText: String? = null
    var dislikedIngredients: MutableSet<DislikedIngredient> = mutableSetOf()
    var otherDislikedIngredientText: String? = null
    var selectedDiseases: MutableSet<Disease> = mutableSetOf()
    var otherDiseaseText: String? = null
    var currentStep: Int = 1
    
    fun clear() {
        selectedCuisines.clear()
        otherCuisineText = null
        selectedDietaryStyle = null
        otherDietaryStyleText = null
        avoidedIngredients.clear()
        otherIngredientText = null
        dislikedIngredients.clear()
        otherDislikedIngredientText = null
        selectedDiseases.clear()
        otherDiseaseText = null
        currentStep = 1
    }
}
