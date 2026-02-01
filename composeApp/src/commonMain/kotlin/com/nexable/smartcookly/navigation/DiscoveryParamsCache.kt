package com.nexable.smartcookly.navigation

import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.recipes.presentation.DiscoveryMode

data class DiscoveryParams(
    val discoveryMode: DiscoveryMode,
    val cuisines: Set<Cuisine>
)

object DiscoveryParamsCache {
    private var cachedParams: DiscoveryParams? = null
    
    fun storeParams(params: DiscoveryParams) {
        cachedParams = params
    }
    
    fun getParams(): DiscoveryParams? {
        return cachedParams
    }
    
    fun clearParams() {
        cachedParams = null
    }
}
