package com.nexable.smartcookly.feature.subscription.data

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SubscriptionRepository {
    
    // Check if user has "Smart Cookly Pro" entitlement
    suspend fun isProUser(): Boolean {
        return try {
            val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
            customerInfo.entitlements["Smart Cookly Pro"]?.isActive == true
        } catch (e: Exception) {
            println("SubscriptionRepository: Error checking pro status: ${e.message}")
            false
        }
    }
    
    // Get customer info as a Flow for reactive UI
    val customerInfoFlow: Flow<CustomerInfo> = callbackFlow {
        // Emit current info immediately
        val current = Purchases.sharedInstance.awaitCustomerInfo()
        trySend(current)

        // Listen for updates via delegate
        Purchases.sharedInstance.delegate = object : PurchasesDelegate {
            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
                trySend(customerInfo)
            }
            override fun onPurchasePromoProduct(
                product: StoreProduct,
                startPurchase: (
                    onError: (error: PurchasesError, userCancelled: Boolean) -> Unit,
                    onSuccess: (storeTransaction: StoreTransaction, customerInfo: CustomerInfo) -> Unit
                ) -> Unit
            ) {
                // No-op: we don't handle promotional purchases in this repository
            }
        }

        awaitClose { Purchases.sharedInstance.delegate = null }
    }
    
    // Login (link Firebase UID to RevenueCat)
    suspend fun login(firebaseUid: String): CustomerInfo {
        return try {
            Purchases.sharedInstance.awaitLogIn(firebaseUid).customerInfo
        } catch (e: Exception) {
            println("SubscriptionRepository: Login error: ${e.message}")
            throw e
        }
    }
    
    // Logout (revert to anonymous)
    suspend fun logout(): CustomerInfo {
        return try {
            Purchases.sharedInstance.awaitLogOut()
        } catch (e: Exception) {
            println("SubscriptionRepository: Logout error: ${e.message}")
            throw e
        }
    }
    
    // Get current offerings
    suspend fun getOfferings(): Offerings {
        return try {
            Purchases.sharedInstance.awaitOfferings()
        } catch (e: Exception) {
            println("SubscriptionRepository: Error getting offerings: ${e.message}")
            throw e
        }
    }
}
