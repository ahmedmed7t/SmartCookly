package com.nexable.smartcookly.feature.subscription.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.subscription.data.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    init {
        observeCustomerInfo()
        loginToRevenueCat()
    }
    
    private fun loginToRevenueCat() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid ?: return@launch
            try {
                subscriptionRepository.login(uid)
            } catch (e: Exception) {
                println("SubscriptionViewModel: RevenueCat login failed: ${e.message}")
            }
        }
    }
    
    private fun observeCustomerInfo() {
        viewModelScope.launch {
            subscriptionRepository.customerInfoFlow.collect { info ->
                val isPro = info.entitlements["Smart Cookly Pro"]?.isActive == true
                _uiState.update { it.copy(isProUser = isPro, isLoading = false) }
            }
        }
    }
    
    fun showPaywall() {
        _uiState.update { it.copy(showPaywall = true) }
    }
    
    fun hidePaywall() {
        _uiState.update { it.copy(showPaywall = false) }
    }
}

data class SubscriptionUiState(
    val isProUser: Boolean = false,
    val isLoading: Boolean = true,
    val showPaywall: Boolean = false
)
