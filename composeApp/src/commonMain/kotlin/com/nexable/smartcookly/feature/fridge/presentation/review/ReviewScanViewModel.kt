package com.nexable.smartcookly.feature.fridge.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.remote.OpenAIApiClient
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.netwrokUtils.onError
import com.nexable.smartcookly.netwrokUtils.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewScanViewModel(
    private val openAIApiClient: OpenAIApiClient,
    private val repository: FridgeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewScanUiState())
    val uiState: StateFlow<ReviewScanUiState> = _uiState.asStateFlow()

    private val _reviewedItems = mutableListOf<FridgeItem>()
    private val _autoSavedItems = mutableListOf<FridgeItem>()

    fun analyzeImage(imageBase64: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            openAIApiClient.analyzeImage(imageBase64)
                .onSuccess { items ->
                    // Split items into reviewed (empty) and auto-saved (all items initially)
                    _reviewedItems.clear()
                    _autoSavedItems.clear()
                    _autoSavedItems.addAll(items)
                    
                    _uiState.value = _uiState.value.copy(
                        detectedItems = items,
                        reviewedItems = _reviewedItems.toList(),
                        autoSavedItems = _autoSavedItems.toList(),
                        isLoading = false,
                        accuracy = calculateAccuracy(items.size)
                    )
                }
                .onError { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.toString()
                    )
                }
        }
    }

    fun removeItem(itemId: String) {
        val items = _uiState.value.detectedItems.toMutableList()
        items.removeAll { it.id == itemId }
        _reviewedItems.removeAll { it.id == itemId }
        _autoSavedItems.removeAll { it.id == itemId }
        
        _uiState.value = _uiState.value.copy(
            detectedItems = items,
            reviewedItems = _reviewedItems.toList(),
            autoSavedItems = _autoSavedItems.toList()
        )
    }

    fun saveToFridge() {
        viewModelScope.launch {
            val allItems = _reviewedItems + _autoSavedItems
            repository.addItems(allItems)
        }
    }

    private fun calculateAccuracy(itemCount: Int): Int {
        // Simple accuracy calculation - in real app would be based on AI confidence
        return when {
            itemCount >= 10 -> 98
            itemCount >= 5 -> 95
            itemCount >= 3 -> 92
            else -> 90
        }
    }
}
