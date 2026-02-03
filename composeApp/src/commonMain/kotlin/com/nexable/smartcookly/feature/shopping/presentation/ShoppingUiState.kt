package com.nexable.smartcookly.feature.shopping.presentation

import com.nexable.smartcookly.feature.shopping.data.model.ShoppingItem

data class ShoppingUiState(
    val items: List<ShoppingItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDeleting: String? = null, // itemId being deleted
    val isDeletingAll: Boolean = false,
    val isAdding: Boolean = false
) {
    val isEmpty: Boolean
        get() = items.isEmpty() && !isLoading && error == null
}
