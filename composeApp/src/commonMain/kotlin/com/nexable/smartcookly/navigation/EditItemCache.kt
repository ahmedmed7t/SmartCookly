package com.nexable.smartcookly.navigation

import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem

object EditItemCache {
    private var cachedEditItem: FridgeItem? = null
    
    fun storeEditItem(item: FridgeItem) {
        cachedEditItem = item
    }
    
    fun getEditItem(): FridgeItem? {
        return cachedEditItem
    }
    
    fun clearEditItem() {
        cachedEditItem = null
    }
}
