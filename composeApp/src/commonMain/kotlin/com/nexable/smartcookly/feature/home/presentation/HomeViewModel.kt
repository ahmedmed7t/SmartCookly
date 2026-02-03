package com.nexable.smartcookly.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.fridge.data.model.FreshStatus
import com.nexable.smartcookly.feature.fridge.data.repository.IngredientRepository
import com.nexable.smartcookly.feature.shopping.data.model.Urgency
import com.nexable.smartcookly.feature.shopping.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val ingredientRepository: IngredientRepository,
    private val shoppingRepository: ShoppingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
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
                // Load expiring items from fridge (from Firestore)
                val allFridgeItems = ingredientRepository.getIngredients(userId)
                val expiringItems = allFridgeItems.filter { item ->
                    val status = item.calculateFreshStatus()
                    status == FreshStatus.URGENT || status == FreshStatus.EXPIRED
                }
                
                // Load urgent shopping items
                val allShoppingItems = shoppingRepository.getItems(userId)
                val urgentShoppingItems = allShoppingItems.filter { it.urgency == Urgency.HIGH }
                
                _uiState.update {
                    it.copy(
                        expiringItems = expiringItems,
                        urgentShoppingItems = urgentShoppingItems,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun refresh() {
        loadData()
    }
}
