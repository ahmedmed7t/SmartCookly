package com.nexable.smartcookly.feature.shopping.presentation.add

import com.nexable.smartcookly.feature.shopping.data.model.Urgency

data class AddShoppingItemUiState(
    val name: String = "",
    val urgency: Urgency = Urgency.NORMAL,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SaveShoppingItemSuccessEvent {
    data object Added : SaveShoppingItemSuccessEvent()
}
