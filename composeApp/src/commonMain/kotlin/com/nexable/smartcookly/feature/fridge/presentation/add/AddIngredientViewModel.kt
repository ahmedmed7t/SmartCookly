package com.nexable.smartcookly.feature.fridge.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.repository.IngredientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddIngredientViewModel(
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddIngredientUiState())
    val uiState: StateFlow<AddIngredientUiState> = _uiState.asStateFlow()
    
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            error = null
        )
    }
    
    fun updateCategory(category: FoodCategory) {
        _uiState.value = _uiState.value.copy(
            category = category,
            error = null
        )
    }
    
    fun updateExpirationDate(date: LocalDate?) {
        _uiState.value = _uiState.value.copy(
            expirationDate = date,
            error = null
        )
    }
    
    fun clearForm() {
        _uiState.value = AddIngredientUiState()
    }
    
    fun clearSuccessFlag() {
        _uiState.value = _uiState.value.copy(isSaveSuccess = false)
    }
    
    fun saveIngredient() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validation
            if (state.name.isBlank()) {
                _uiState.value = state.copy(
                    error = "Ingredient name is required"
                )
                return@launch
            }
            
            if (state.category == null) {
                _uiState.value = state.copy(
                    error = "Please select a category"
                )
                return@launch
            }
            
            val userId = authRepository.getCurrentUser()?.uid
            if (userId == null) {
                _uiState.value = state.copy(
                    error = "User not authenticated"
                )
                return@launch
            }
            
            _uiState.value = state.copy(isLoading = true, error = null)
            
            try {
                val fridgeItem = FridgeItem(
                    name = state.name.trim(),
                    category = state.category,
                    expirationDate = state.expirationDate
                )
                
                ingredientRepository.addIngredient(userId, fridgeItem)
                
                // Clear form and set success flag
                _uiState.value = AddIngredientUiState(
                    isSaveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save ingredient"
                )
            }
        }
    }
}
