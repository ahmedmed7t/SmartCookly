package com.nexable.smartcookly.feature.fridge.presentation.review

import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem

data class ReviewScanUiState(
    val detectedItems: List<FridgeItem> = emptyList(),
    val reviewedItems: List<FridgeItem> = emptyList(),
    val autoSavedItems: List<FridgeItem> = emptyList(),
    val isLoading: Boolean = false,
    val accuracy: Int? = null,
    val error: String? = null
)
