package com.nexable.smartcookly.feature.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingItem(
    val id: String = "",
    val name: String = "",
    val urgency: Urgency = Urgency.NORMAL,
    val addedAt: Long = 0
)

@Serializable
enum class Urgency {
    LOW,      // "Can wait"
    NORMAL,   // "Need soon"  
    HIGH      // "Urgent"
}
