package com.nexable.smartcookly.feature.shopping.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.shopping.data.model.ShoppingItem
import com.nexable.smartcookly.feature.shopping.data.model.Urgency
import com.nexable.smartcookly.feature.shopping.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShoppingViewModel(
    private val shoppingRepository: ShoppingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()
    
    init {
        loadItems()
    }
    
    fun loadItems() {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "User not authenticated"
                )
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val items = shoppingRepository.getItems(userId)
                _uiState.update {
                    it.copy(
                        items = items,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load shopping list: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun addItem(name: String, urgency: Urgency) {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            _uiState.update {
                it.copy(error = "User not authenticated")
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isAdding = true, error = null) }
            
            try {
                val trimmedName = name.trim()
                if (trimmedName.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isAdding = false,
                            error = "Ingredient name cannot be empty"
                        )
                    }
                    return@launch
                }
                
                // Check if item with same name already exists (case-insensitive)
                val itemExists = shoppingRepository.itemExists(userId, trimmedName)
                if (itemExists) {
                    _uiState.update {
                        it.copy(
                            isAdding = false,
                            error = "\"$trimmedName\" is already in your shopping list"
                        )
                    }
                    return@launch
                }
                
                val item = ShoppingItem(
                    name = trimmedName,
                    urgency = urgency,
                    addedAt = System.currentTimeMillis()
                )
                shoppingRepository.addItem(userId, item)
                // Reset adding state after successful add
                _uiState.update { it.copy(isAdding = false, error = null) }
                // Reload items after adding
                loadItems()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAdding = false,
                        error = "Failed to add item: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun deleteItem(itemId: String) {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            _uiState.update {
                it.copy(error = "User not authenticated")
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = itemId) }
            
            try {
                shoppingRepository.deleteItem(userId, itemId)
                // Reload items after deletion
                loadItems()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = null,
                        error = "Failed to delete item: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun deleteAllItems() {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            _uiState.update {
                it.copy(error = "User not authenticated")
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAll = true) }
            
            try {
                shoppingRepository.deleteAllItems(userId)
                // Reload items after deletion
                loadItems()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeletingAll = false,
                        error = "Failed to delete all items: ${e.message}"
                    )
                }
            }
        }
    }
}
