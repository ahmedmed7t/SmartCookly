package com.nexable.smartcookly.feature.fridge.presentation.fridge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.fridge.data.repository.IngredientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FridgeViewModel(
    private val repository: FridgeRepository,
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)
    val selectedCategory: StateFlow<FoodCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    init {
        loadIngredients()
    }

    val uiState: StateFlow<FridgeUiState> = combine(
        repository.items,
        _selectedCategory,
        _searchQuery,
        _isLoading
    ) { items, category, query, isLoading ->
        val filteredItems = items
            .filter { item ->
                (category == null || item.category == category) &&
                (query.isEmpty() || item.name.contains(query, ignoreCase = true))
            }
        
        val groupedItems = filteredItems.groupBy { it.category }
        
        FridgeUiState(
            items = filteredItems,
            groupedItems = groupedItems,
            selectedCategory = category,
            searchQuery = query,
            isLoading = isLoading,
            totalItemCount = items.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FridgeUiState()
    )

    fun selectCategory(category: FoodCategory?) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addItem(item: FridgeItem) {
        viewModelScope.launch {
            repository.addItem(item)
        }
    }

    fun addItems(items: List<FridgeItem>) {
        viewModelScope.launch {
            repository.addItems(items)
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid ?: return@launch
            try {
                ingredientRepository.deleteIngredient(userId, itemId)
                // Reload ingredients after deletion
                loadIngredients()
            } catch (e: Exception) {
                println("FridgeViewModel: Error deleting item - ${e.message}")
                // Handle error - could show error state if needed
            }
        }
    }

    fun loadIngredients() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid ?: return@launch
            _isLoading.value = true
            try {
                val items = ingredientRepository.getIngredients(userId)
                // Replace all items with fetched items from Firebase
                repository.setItems(items)
            } catch (e: Exception) {
                // Handle error silently or show error state if needed
                println("FridgeViewModel: Error loading ingredients - ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
