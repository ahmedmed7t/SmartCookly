package com.nexable.smartcookly.feature.onboarding.data

import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient

object OnboardingDataCache {
    var selectedCuisines: MutableSet<Cuisine> = mutableSetOf()
    var otherCuisineText: String? = null
    var selectedDietaryStyle: DietaryStyle? = null
    var otherDietaryStyleText: String? = null
    var avoidedIngredients: MutableSet<Ingredient> = mutableSetOf()
    var otherIngredientText: String? = null
    
    fun clear() {
        selectedCuisines.clear()
        otherCuisineText = null
        selectedDietaryStyle = null
        otherDietaryStyleText = null
        avoidedIngredients.clear()
        otherIngredientText = null
    }
}
