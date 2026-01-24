package com.nexable.smartcookly.feature.fridge.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetectedFoodItem(
    val name: String,
    val category: String,
    @SerialName("estimated_days_until_expiration")
    val estimatedDaysToExpiration: Int? = null
)
