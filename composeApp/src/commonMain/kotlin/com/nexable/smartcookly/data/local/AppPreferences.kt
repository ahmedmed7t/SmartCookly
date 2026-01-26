package com.nexable.smartcookly.data.local

import com.nexable.smartcookly.feature.onboarding.data.model.CookingLevel
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.data.model.Disease
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient
import com.nexable.smartcookly.feature.user.data.model.UserProfile
import com.russhwolf.settings.Settings

class AppPreferences(private val settings: Settings) {
    fun isOnboardingCompleted(): Boolean = 
        settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    
    fun setOnboardingCompleted(completed: Boolean) {
        settings.putBoolean(KEY_ONBOARDING_COMPLETED, completed)
    }
    
    fun isGuestMode(): Boolean = 
        settings.getBoolean(KEY_GUEST_MODE, false)
    
    fun setGuestMode(enabled: Boolean) {
        settings.putBoolean(KEY_GUEST_MODE, enabled)
    }
    
    // Onboarding data persistence
    fun saveOnboardingData(
        currentStep: Int,
        selectedCuisines: Set<Cuisine>,
        otherCuisineText: String?,
        selectedDietaryStyle: DietaryStyle?,
        otherDietaryStyleText: String?,
        avoidedIngredients: Set<Ingredient>,
        otherIngredientText: String?,
        dislikedIngredients: Set<DislikedIngredient>,
        otherDislikedIngredientText: String?,
        selectedDiseases: Set<Disease>,
        otherDiseaseText: String?,
        selectedCookingLevel: CookingLevel?
    ) {
        settings.putInt(KEY_CURRENT_STEP, currentStep)
        settings.putString(KEY_SELECTED_CUISINES, selectedCuisines.joinToString(",") { it.name })
        settings.putString(KEY_OTHER_CUISINE_TEXT, otherCuisineText ?: "")
        settings.putString(KEY_SELECTED_DIETARY_STYLE, selectedDietaryStyle?.name ?: "")
        settings.putString(KEY_OTHER_DIETARY_STYLE_TEXT, otherDietaryStyleText ?: "")
        settings.putString(KEY_AVOIDED_INGREDIENTS, avoidedIngredients.joinToString(",") { it.name })
        settings.putString(KEY_OTHER_INGREDIENT_TEXT, otherIngredientText ?: "")
        settings.putString(KEY_DISLIKED_INGREDIENTS, dislikedIngredients.joinToString(",") { it.name })
        settings.putString(KEY_OTHER_DISLIKED_INGREDIENT_TEXT, otherDislikedIngredientText ?: "")
        settings.putString(KEY_SELECTED_DISEASES, selectedDiseases.joinToString(",") { it.name })
        settings.putString(KEY_OTHER_DISEASE_TEXT, otherDiseaseText ?: "")
        settings.putString(KEY_SELECTED_COOKING_LEVEL, selectedCookingLevel?.name ?: "")
    }
    
    fun loadOnboardingData(): OnboardingData {
        val currentStep = settings.getInt(KEY_CURRENT_STEP, 1)
        val selectedCuisines = settings.getString(KEY_SELECTED_CUISINES, "")
            .takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.mapNotNull { runCatching { Cuisine.valueOf(it) }.getOrNull() }
            ?.toSet() ?: emptySet()
        
        val otherCuisineText = settings.getString(KEY_OTHER_CUISINE_TEXT, "")
            .takeIf { it.isNotEmpty() }
        
        val selectedDietaryStyle = settings.getString(KEY_SELECTED_DIETARY_STYLE, "")
            .takeIf { it.isNotEmpty() }
            ?.let { runCatching { DietaryStyle.valueOf(it) }.getOrNull() }
        
        val otherDietaryStyleText = settings.getString(KEY_OTHER_DIETARY_STYLE_TEXT, "")
            .takeIf { it.isNotEmpty() }
        
        val avoidedIngredients = settings.getString(KEY_AVOIDED_INGREDIENTS, "")
            .takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.mapNotNull { runCatching { Ingredient.valueOf(it) }.getOrNull() }
            ?.toSet() ?: emptySet()
        
        val otherIngredientText = settings.getString(KEY_OTHER_INGREDIENT_TEXT, "")
            .takeIf { it.isNotEmpty() }
        
        val dislikedIngredients = settings.getString(KEY_DISLIKED_INGREDIENTS, "")
            .takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.mapNotNull { runCatching { DislikedIngredient.valueOf(it) }.getOrNull() }
            ?.toSet() ?: emptySet()
        
        val otherDislikedIngredientText = settings.getString(KEY_OTHER_DISLIKED_INGREDIENT_TEXT, "")
            .takeIf { it.isNotEmpty() }
        
        val selectedDiseases = settings.getString(KEY_SELECTED_DISEASES, "")
            .takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.mapNotNull { runCatching { Disease.valueOf(it) }.getOrNull() }
            ?.toSet() ?: emptySet()
        
        val otherDiseaseText = settings.getString(KEY_OTHER_DISEASE_TEXT, "")
            .takeIf { it.isNotEmpty() }
        
        val selectedCookingLevel = settings.getString(KEY_SELECTED_COOKING_LEVEL, "")
            .takeIf { it.isNotEmpty() }
            ?.let { runCatching { CookingLevel.valueOf(it) }.getOrNull() }
        
        return OnboardingData(
            currentStep = currentStep,
            selectedCuisines = selectedCuisines,
            otherCuisineText = otherCuisineText,
            selectedDietaryStyle = selectedDietaryStyle,
            otherDietaryStyleText = otherDietaryStyleText,
            avoidedIngredients = avoidedIngredients,
            otherIngredientText = otherIngredientText,
            dislikedIngredients = dislikedIngredients,
            otherDislikedIngredientText = otherDislikedIngredientText,
            selectedDiseases = selectedDiseases,
            otherDiseaseText = otherDiseaseText,
            selectedCookingLevel = selectedCookingLevel
        )
    }
    
    fun clearOnboardingData() {
        settings.remove(KEY_CURRENT_STEP)
        settings.remove(KEY_SELECTED_CUISINES)
        settings.remove(KEY_OTHER_CUISINE_TEXT)
        settings.remove(KEY_SELECTED_DIETARY_STYLE)
        settings.remove(KEY_OTHER_DIETARY_STYLE_TEXT)
        settings.remove(KEY_AVOIDED_INGREDIENTS)
        settings.remove(KEY_OTHER_INGREDIENT_TEXT)
        settings.remove(KEY_DISLIKED_INGREDIENTS)
        settings.remove(KEY_OTHER_DISLIKED_INGREDIENT_TEXT)
        settings.remove(KEY_SELECTED_DISEASES)
        settings.remove(KEY_OTHER_DISEASE_TEXT)
        settings.remove(KEY_SELECTED_COOKING_LEVEL)
    }
    
    fun toUserProfile(): UserProfile {
        val onboardingData = loadOnboardingData()
        return UserProfile.fromOnboardingData(onboardingData)
    }
    
    fun updateFromUserProfile(profile: UserProfile) {
        val cuisines = profile.cuisines.mapNotNull { 
            runCatching { Cuisine.valueOf(it) }.getOrNull() 
        }.toSet()
        
        val dietaryStyle = profile.dietaryStyle?.let { 
            runCatching { DietaryStyle.valueOf(it) }.getOrNull() 
        }
        
        val avoidedIngredients = profile.avoidedIngredients.mapNotNull { 
            runCatching { Ingredient.valueOf(it) }.getOrNull() 
        }.toSet()
        
        val dislikedIngredients = profile.dislikedIngredients.mapNotNull { 
            runCatching { DislikedIngredient.valueOf(it) }.getOrNull() 
        }.toSet()
        
        val diseases = profile.diseases.mapNotNull { 
            runCatching { Disease.valueOf(it) }.getOrNull() 
        }.toSet()
        
        val cookingLevel = profile.cookingLevel?.let { 
            runCatching { CookingLevel.valueOf(it) }.getOrNull() 
        }
        
        saveOnboardingData(
            currentStep = 1, // Reset step when syncing from Firestore
            selectedCuisines = cuisines,
            otherCuisineText = profile.otherCuisineText,
            selectedDietaryStyle = dietaryStyle,
            otherDietaryStyleText = profile.otherDietaryStyleText,
            avoidedIngredients = avoidedIngredients,
            otherIngredientText = profile.otherIngredientText,
            dislikedIngredients = dislikedIngredients,
            otherDislikedIngredientText = profile.otherDislikedIngredientText,
            selectedDiseases = diseases,
            otherDiseaseText = profile.otherDiseaseText,
            selectedCookingLevel = cookingLevel
        )
    }
    
    data class OnboardingData(
        val currentStep: Int,
        val selectedCuisines: Set<Cuisine>,
        val otherCuisineText: String?,
        val selectedDietaryStyle: DietaryStyle?,
        val otherDietaryStyleText: String?,
        val avoidedIngredients: Set<Ingredient>,
        val otherIngredientText: String?,
        val dislikedIngredients: Set<DislikedIngredient>,
        val otherDislikedIngredientText: String?,
        val selectedDiseases: Set<Disease>,
        val otherDiseaseText: String?,
        val selectedCookingLevel: CookingLevel?
    )
    
    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_GUEST_MODE = "guest_mode"
        private const val KEY_CURRENT_STEP = "onboarding_current_step"
        private const val KEY_SELECTED_CUISINES = "onboarding_selected_cuisines"
        private const val KEY_OTHER_CUISINE_TEXT = "onboarding_other_cuisine_text"
        private const val KEY_SELECTED_DIETARY_STYLE = "onboarding_selected_dietary_style"
        private const val KEY_OTHER_DIETARY_STYLE_TEXT = "onboarding_other_dietary_style_text"
        private const val KEY_AVOIDED_INGREDIENTS = "onboarding_avoided_ingredients"
        private const val KEY_OTHER_INGREDIENT_TEXT = "onboarding_other_ingredient_text"
        private const val KEY_DISLIKED_INGREDIENTS = "onboarding_disliked_ingredients"
        private const val KEY_OTHER_DISLIKED_INGREDIENT_TEXT = "onboarding_other_disliked_ingredient_text"
        private const val KEY_SELECTED_DISEASES = "onboarding_selected_diseases"
        private const val KEY_OTHER_DISEASE_TEXT = "onboarding_other_disease_text"
        private const val KEY_SELECTED_COOKING_LEVEL = "onboarding_selected_cooking_level"
    }
}
