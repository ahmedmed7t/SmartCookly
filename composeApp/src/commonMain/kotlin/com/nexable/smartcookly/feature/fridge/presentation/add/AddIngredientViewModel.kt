package com.nexable.smartcookly.feature.fridge.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.repository.IngredientRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddIngredientViewModel(
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddIngredientUiState())
    val uiState: StateFlow<AddIngredientUiState> = _uiState.asStateFlow()
    
    private val _saveSuccessEvent = MutableSharedFlow<SaveSuccessEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val saveSuccessEvent: SharedFlow<SaveSuccessEvent> = _saveSuccessEvent.asSharedFlow()
    
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
    
    fun loadItem(item: FridgeItem) {
        _uiState.value = AddIngredientUiState(
            name = item.name,
            category = item.category,
            expirationDate = item.expirationDate,
            editingItemId = item.id
        )
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
                val fridgeItem = if (state.isEditMode && state.editingItemId != null) {
                    // Update existing item
                    FridgeItem(
                        id = state.editingItemId,
                        name = state.name.trim(),
                        category = state.category,
                        expirationDate = state.expirationDate
                    )
                } else {
                    // Create new item
                    FridgeItem(
                        name = state.name.trim(),
                        category = state.category,
                        expirationDate = state.expirationDate
                    )
                }
                
                val wasEditMode = state.isEditMode
                val editingItemId = state.editingItemId
                
                if (wasEditMode) {
                    ingredientRepository.updateIngredient(userId, fridgeItem)
                } else {
                    ingredientRepository.addIngredient(userId, fridgeItem)
                }
                
                // Emit success event
                if (wasEditMode && editingItemId != null) {
                    // In edit mode, emit update event with editingItemId
                    _saveSuccessEvent.emit(SaveSuccessEvent.Updated(editingItemId))
                    // Clear form fields but preserve edit mode flag for navigation check
                    _uiState.value = AddIngredientUiState(
                        editingItemId = editingItemId
                    )
                } else {
                    // In add mode, emit add event and clear form completely
                    _saveSuccessEvent.emit(SaveSuccessEvent.Added)
                    _uiState.value = AddIngredientUiState()
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: if (state.isEditMode) "Failed to update ingredient" else "Failed to save ingredient"
                )
            }
        }
    }
}
