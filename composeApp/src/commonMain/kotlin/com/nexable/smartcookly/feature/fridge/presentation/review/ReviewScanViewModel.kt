package com.nexable.smartcookly.feature.fridge.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.remote.OpenAIApiClient
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.fridge.data.repository.IngredientRepository
import com.nexable.smartcookly.feature.fridge.data.repository.ImageStorageRepository
import com.nexable.smartcookly.netwrokUtils.onError
import com.nexable.smartcookly.netwrokUtils.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewScanViewModel(
    imageBytes: ByteArray,
    private val openAIApiClient: OpenAIApiClient,
    private val repository: FridgeRepository,
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository,
    private val imageStorageRepository: ImageStorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewScanUiState())
    val uiState: StateFlow<ReviewScanUiState> = _uiState.asStateFlow()

    private val _reviewedItems = mutableListOf<FridgeItem>()
    private val _autoSavedItems = mutableListOf<FridgeItem>()

    init {
        analyzeImage(imageBytes)
    }

    fun analyzeImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "User not authenticated"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Upload image to Firebase Storage
                val downloadUrl = imageStorageRepository.uploadScanImage(userId, imageBytes)
                
                // Analyze with OpenAI using URL
                openAIApiClient.analyzeImageFromUrl(downloadUrl)
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error uploading image: ${e.message}"
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
    
    fun updateItem(updatedItem: FridgeItem) {
        val items = _uiState.value.detectedItems.toMutableList()
        val index = items.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            items[index] = updatedItem
            // Also update in reviewedItems and autoSavedItems if present
            val reviewedIndex = _reviewedItems.indexOfFirst { it.id == updatedItem.id }
            if (reviewedIndex != -1) {
                _reviewedItems[reviewedIndex] = updatedItem
            }
            val autoSavedIndex = _autoSavedItems.indexOfFirst { it.id == updatedItem.id }
            if (autoSavedIndex != -1) {
                _autoSavedItems[autoSavedIndex] = updatedItem
            }
            
            _uiState.value = _uiState.value.copy(
                detectedItems = items,
                reviewedItems = _reviewedItems.toList(),
                autoSavedItems = _autoSavedItems.toList()
            )
        }
    }

    fun saveToFridge() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    error = "User not authenticated"
                )
                return@launch
            }
            
            val allItems = _reviewedItems + _autoSavedItems
            
            // Save to Firebase
            allItems.forEach { item ->
                try {
                    ingredientRepository.addIngredient(userId, item)
                } catch (e: Exception) {
                    println("ReviewScanViewModel: Error saving item ${item.name}: ${e.message}")
                }
            }
            
            // Also update local repository
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
