package com.nexable.smartcookly.feature.fridge.presentation.add

import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import kotlinx.datetime.LocalDate

data class AddIngredientUiState(
    val name: String = "",
    val category: FoodCategory? = null,
    val expirationDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaveSuccess: Boolean = false
)
