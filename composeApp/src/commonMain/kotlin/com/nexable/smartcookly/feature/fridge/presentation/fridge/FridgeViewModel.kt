package com.nexable.smartcookly.feature.fridge.presentation.fridge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FridgeViewModel(
    private val repository: FridgeRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)
    val selectedCategory: StateFlow<FoodCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<FridgeUiState> = combine(
        repository.items,
        _selectedCategory,
        _searchQuery
    ) { items, category, query ->
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
            repository.deleteItem(itemId)
        }
    }
}
