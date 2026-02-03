package com.nexable.smartcookly.feature.home.presentation

import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.shopping.data.model.ShoppingItem

data class HomeUiState(
    val expiringItems: List<FridgeItem> = emptyList(),
    val urgentShoppingItems: List<ShoppingItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val hasExpiringItems: Boolean
        get() = expiringItems.isNotEmpty()
    
    val hasUrgentShoppingItems: Boolean
        get() = urgentShoppingItems.isNotEmpty()
}
