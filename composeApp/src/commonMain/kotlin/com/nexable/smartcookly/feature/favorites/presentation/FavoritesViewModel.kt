package com.nexable.smartcookly.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.favorites.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    fun loadFavorites() {
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
                val favorites = favoritesRepository.getFavorites(userId)
                _uiState.update {
                    it.copy(
                        favorites = favorites,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load favorites: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun removeFromFavorites(recipeId: String) {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            _uiState.update {
                it.copy(error = "User not authenticated")
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isRemoving = recipeId) }
            
            try {
                favoritesRepository.removeFromFavorites(userId, recipeId)
                // Reload favorites after removal
                loadFavorites()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRemoving = null,
                        error = "Failed to remove recipe: ${e.message}"
                    )
                }
            }
        }
    }
}
