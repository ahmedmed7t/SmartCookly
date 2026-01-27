package com.nexable.smartcookly.feature.fridge.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class FoodCategory(val displayName: String) {
    VEGETABLES("VEGETABLES"),
    FRUITS("FRUITS"),
    PROTEINS("PROTEINS"),
    DAIRY("DAIRY"),
    GRAINS("GRAINS"),
    LEGUMES("LEGUMES"),
    NUTS_SEEDS("NUTS"),
    OILS_FATS("OILS & FATS"),
    HERBS_SPICES("HERBS & SPICES"),
    SAUCES_CONDIMENTS("SAUCES"),
    OTHER("OTHER")
}
