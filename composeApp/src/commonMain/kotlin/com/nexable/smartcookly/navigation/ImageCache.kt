package com.nexable.smartcookly.navigation

object ImageCache {
    private var cachedImage: String? = null
    
    fun storeImage(imageBase64: String) {
        cachedImage = imageBase64
    }
    
    fun getImage(): String? {
        return cachedImage
    }
    
    fun clearImage() {
        cachedImage = null
    }
}
