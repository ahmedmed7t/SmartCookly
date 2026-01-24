package com.nexable.smartcookly.feature.fridge.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class FoodCategory {
    VEGETABLES,
    FRUITS,
    PROTEINS,
    DAIRY,
    GRAINS,
    LEGUMES,
    NUTS_SEEDS,
    OILS_FATS,
    HERBS_SPICES,
    SAUCES_CONDIMENTS,
    OTHER
}
