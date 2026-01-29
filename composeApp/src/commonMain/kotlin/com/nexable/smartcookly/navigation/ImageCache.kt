package com.nexable.smartcookly.navigation

object ImageCache {
    private var cachedImageBytes: ByteArray? = null
    
    fun storeImageBytes(bytes: ByteArray) {
        cachedImageBytes = bytes
    }
    
    fun getImageBytes(): ByteArray? {
        return cachedImageBytes
    }
    
    fun clearImage() {
        cachedImageBytes = null
    }
}
