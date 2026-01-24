package com.nexable.smartcookly.feature.fridge.presentation.fridge

import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem

data class FridgeUiState(
    val items: List<FridgeItem> = emptyList(),
    val groupedItems: Map<FoodCategory, List<FridgeItem>> = emptyMap(),
    val selectedCategory: FoodCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val totalItemCount: Int = 0
)
