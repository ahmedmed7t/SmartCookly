package com.nexable.smartcookly.feature.fridge.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class FoodCategory(val displayName: String, val emoji: String) {
    DAIRY("DAIRY", "🥛"),
    VEGETABLES("VEGETABLES", "🥬"),
    FRUITS("FRUITS", "🍎"),
    MEAT("MEAT", "🥩"),
    SEAFOOD("SEAFOOD", "🐟"),
    GRAINS("GRAINS", "🌾"),
    BEVERAGES("BEVERAGES", "🧃"),
    CONDIMENTS("CONDIMENTS", "🧂"),
    SNACKS("SNACKS", "🍿"),
    FROZEN("FROZEN", "🧊"),
    OTHER("OTHER", "📦");
    
    // Legacy support for old enum values
    companion object {
        fun fromLegacyValue(value: String): FoodCategory {
            return when (value.uppercase()) {
                "PROTEINS" -> MEAT
                "LEGUMES" -> OTHER
                "NUTS_SEEDS", "NUTS" -> SNACKS
                "OILS_FATS" -> CONDIMENTS
                "HERBS_SPICES" -> CONDIMENTS
                "SAUCES_CONDIMENTS", "SAUCES" -> CONDIMENTS
                else -> try {
                    valueOf(value.uppercase())
                } catch (e: IllegalArgumentException) {
                    OTHER
                }
            }
        }
    }
}
