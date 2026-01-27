package com.nexable.smartcookly.feature.fridge.data.repository

import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FridgeRepository {
    private val _items = MutableStateFlow<List<FridgeItem>>(emptyList())
    val items: StateFlow<List<FridgeItem>> = _items.asStateFlow()

    fun getAllItems(): List<FridgeItem> = _items.value

    fun getItemsByCategory(category: FoodCategory?): List<FridgeItem> {
        return if (category == null) {
            _items.value
        } else {
            _items.value.filter { it.category == category }
        }
    }

    fun getItemsGroupedByCategory(): Map<FoodCategory, List<FridgeItem>> {
        return _items.value.groupBy { it.category }
    }

    fun addItem(item: FridgeItem) {
        val currentItems = _items.value.toMutableList()
        
        // Check if item with same name exists - update quantity instead
        val existingIndex = currentItems.indexOfFirst { 
            it.name.equals(item.name, ignoreCase = true) 
        }
        
        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(
                expirationDate = item.expirationDate ?: existing.expirationDate,
                freshStatus = item.calculateFreshStatus()
            )
        } else {
            currentItems.add(item.copy(freshStatus = item.calculateFreshStatus()))
        }
        
        _items.value = currentItems
    }

    fun addItems(items: List<FridgeItem>) {
        items.forEach { addItem(it) }
    }

    fun setItems(items: List<FridgeItem>) {
        _items.value = items.map { it.copy(freshStatus = it.calculateFreshStatus()) }
    }

    fun updateItem(item: FridgeItem) {
        val currentItems = _items.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            currentItems[index] = item.copy(freshStatus = item.calculateFreshStatus())
            _items.value = currentItems
        }
    }

    fun deleteItem(itemId: String) {
        val currentItems = _items.value.toMutableList()
        currentItems.removeAll { it.id == itemId }
        _items.value = currentItems
    }

    fun getItemCount(): Int = _items.value.size

    fun getItemCountByCategory(category: FoodCategory): Int {
        return _items.value.count { it.category == category }
    }
}
