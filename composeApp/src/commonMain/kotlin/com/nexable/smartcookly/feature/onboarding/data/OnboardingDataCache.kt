package com.nexable.smartcookly.feature.onboarding.data

import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle

object OnboardingDataCache {
    var selectedCuisines: MutableSet<Cuisine> = mutableSetOf()
    var otherCuisineText: String? = null
    var selectedDietaryStyle: DietaryStyle? = null
    var otherDietaryStyleText: String? = null
    
    fun clear() {
        selectedCuisines.clear()
        otherCuisineText = null
        selectedDietaryStyle = null
        otherDietaryStyleText = null
    }
}
