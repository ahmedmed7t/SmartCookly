package com.nexable.smartcookly.feature.user.data.model

import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.onboarding.data.model.CookingLevel
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.data.model.Disease
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient
import dev.gitlive.firebase.firestore.Timestamp

data class UserProfile(
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
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        fun fromOnboardingData(onboardingData: AppPreferences.OnboardingData): UserProfile {
            return UserProfile(
                cuisines = onboardingData.selectedCuisines.map { it.name },
                otherCuisineText = onboardingData.otherCuisineText,
                dietaryStyle = onboardingData.selectedDietaryStyle?.name,
                otherDietaryStyleText = onboardingData.otherDietaryStyleText,
                avoidedIngredients = onboardingData.avoidedIngredients.map { it.name },
                otherIngredientText = onboardingData.otherIngredientText,
                dislikedIngredients = onboardingData.dislikedIngredients.map { it.name },
                otherDislikedIngredientText = onboardingData.otherDislikedIngredientText,
                diseases = onboardingData.selectedDiseases.map { it.name },
                otherDiseaseText = onboardingData.otherDiseaseText,
                cookingLevel = onboardingData.selectedCookingLevel?.name
            )
        }
    }
}
