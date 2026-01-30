package com.nexable.smartcookly.feature.recipes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class RecipesViewModel(
    private val appPreferences: AppPreferences,
    private val fridgeRepository: FridgeRepository
) : ViewModel() {
    private val _discoveryMode = MutableStateFlow(DiscoveryMode.PREFERENCES)
    private val _cuisineContext = MutableStateFlow(CuisineContext.FAVORITES)
    private val _favoriteCuisines = MutableStateFlow<Set<Cuisine>>(emptySet())
    private val _selectedOtherCuisines = MutableStateFlow<Set<Cuisine>>(emptySet())
    private val _isBottomSheetVisible = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _fridgeItemsCount = MutableStateFlow(0)

    init {
        loadInitialData()
        // Observe fridge items count
        viewModelScope.launch {
            fridgeRepository.items.collect { items ->
                _fridgeItemsCount.value = items.size
            }
        }
    }

    val uiState: StateFlow<RecipesUiState> = combine(
        combine(
            _discoveryMode,
            _cuisineContext,
            _favoriteCuisines,
            _selectedOtherCuisines,
            _isBottomSheetVisible
        ) { discoveryMode, cuisineContext, favoriteCuisines, selectedOtherCuisines, 
            isBottomSheetVisible ->
            Data(
                discoveryMode = discoveryMode,
                cuisineContext = cuisineContext,
                favoriteCuisines = favoriteCuisines,
                selectedOtherCuisines = selectedOtherCuisines,
                isBottomSheetVisible = isBottomSheetVisible
            )
        },
        combine(_isLoading, _fridgeItemsCount) { isLoading, fridgeItemsCount ->
            Pair(isLoading, fridgeItemsCount)
        }
    ) { firstPart, loadingAndCount ->
        RecipesUiState(
            discoveryMode = firstPart.discoveryMode,
            cuisineContext = firstPart.cuisineContext,
            favoriteCuisines = firstPart.favoriteCuisines,
            selectedOtherCuisines = firstPart.selectedOtherCuisines,
            isBottomSheetVisible = firstPart.isBottomSheetVisible,
            isLoading = loadingAndCount.first,
            fridgeItemsCount = loadingAndCount.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecipesUiState()
    )

    private data class Data(
        val discoveryMode: DiscoveryMode,
        val cuisineContext: CuisineContext,
        val favoriteCuisines: Set<Cuisine>,
        val selectedOtherCuisines: Set<Cuisine>,
        val isBottomSheetVisible: Boolean
    )

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load favorite cuisines from onboarding preferences
            val onboardingData = appPreferences.loadOnboardingData()
            _favoriteCuisines.value = onboardingData.selectedCuisines
        }
    }

    fun selectDiscoveryMode(mode: DiscoveryMode) {
        _discoveryMode.value = mode
    }

    fun selectCuisineContext(context: CuisineContext) {
        _cuisineContext.value = context
        
        // If selecting "Select Others" and bottom sheet not shown yet, show it
        if (context == CuisineContext.SELECT_OTHERS && !_isBottomSheetVisible.value) {
            showBottomSheet()
        }
    }

    fun updateSelectedCuisines(cuisines: Set<Cuisine>) {
        _selectedOtherCuisines.value = cuisines
    }

    fun showBottomSheet() {
        _isBottomSheetVisible.value = true
    }

    fun hideBottomSheet() {
        _isBottomSheetVisible.value = false
    }

    fun discoverRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // TODO: Implement actual recipe discovery API call
            // For now, just simulate loading
            kotlinx.coroutines.delay(2000)
            
            _isLoading.value = false
        }
    }
}
