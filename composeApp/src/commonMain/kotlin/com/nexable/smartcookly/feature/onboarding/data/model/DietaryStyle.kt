package com.nexable.smartcookly.feature.onboarding.data.model

enum class DietaryStyle(val displayName: String, val description: String, val emoji: String) {
    OMNIVORE("Omnivore", "Eats all foods", "🍽️"),
    VEGETARIAN("Vegetarian", "No meat or fish", "🥗"),
    VEGAN("Vegan", "No animal products", "🌱"),
    PESCATARIAN("Pescatarian", "Fish, no meat", "🐟"),
    KETO("Keto", "Very low carb", "🥑"),
    LOW_CARB("Low-Carb", "Reduced carbs", "🥩"),
    HIGH_PROTEIN("High-Protein", "Protein focused", "💪"),
    MEDITERRANEAN("Mediterranean", "Healthy balanced diet", "🫒"),
    OTHER("Other", "Other dietary preferences", "➕")
}
