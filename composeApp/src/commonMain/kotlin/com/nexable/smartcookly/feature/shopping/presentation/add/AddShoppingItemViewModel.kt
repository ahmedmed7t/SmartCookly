package com.nexable.smartcookly.feature.shopping.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.shopping.data.model.ShoppingItem
import com.nexable.smartcookly.feature.shopping.data.model.Urgency
import com.nexable.smartcookly.feature.shopping.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddShoppingItemViewModel(
    private val shoppingRepository: ShoppingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddShoppingItemUiState())
    val uiState: StateFlow<AddShoppingItemUiState> = _uiState.asStateFlow()
    
    private val _saveSuccessEvent = MutableSharedFlow<SaveShoppingItemSuccessEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val saveSuccessEvent: SharedFlow<SaveShoppingItemSuccessEvent> = _saveSuccessEvent.asSharedFlow()
    
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            error = null
        )
    }
    
    fun updateUrgency(urgency: Urgency) {
        _uiState.value = _uiState.value.copy(
            urgency = urgency,
            error = null
        )
    }
    
    fun clearForm() {
        _uiState.value = AddShoppingItemUiState()
    }
    
    fun saveItem() {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            _uiState.value = _uiState.value.copy(
                error = "User not authenticated"
            )
            return
        }
        
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validation
            val trimmedName = state.name.trim()
            if (trimmedName.isBlank()) {
                _uiState.value = state.copy(
                    error = "Item name cannot be empty"
                )
                return@launch
            }
            
            _uiState.value = state.copy(isLoading = true, error = null)
            
            try {
                // Check if item with same name already exists (case-insensitive)
                val itemExists = shoppingRepository.itemExists(userId, trimmedName)
                if (itemExists) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "\"$trimmedName\" is already in your shopping list"
                    )
                    return@launch
                }
                
                val item = ShoppingItem(
                    name = trimmedName,
                    urgency = state.urgency,
                    addedAt = System.currentTimeMillis()
                )
                
                shoppingRepository.addItem(userId, item)
                
                // Emit success event and clear form
                _saveSuccessEvent.emit(SaveShoppingItemSuccessEvent.Added)
                _uiState.value = AddShoppingItemUiState()
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Failed to add item: ${e.message}"
                )
            }
        }
    }
}
