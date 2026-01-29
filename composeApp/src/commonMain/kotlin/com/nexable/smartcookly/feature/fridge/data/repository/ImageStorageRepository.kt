package com.nexable.smartcookly.feature.fridge.data.repository

import com.nexable.smartcookly.platform.ImageUploader

class ImageStorageRepository(
    private val imageUploader: ImageUploader
) {
    suspend fun uploadScanImage(userId: String, imageBytes: ByteArray): String {
        return imageUploader.uploadJpegBytes(userId, imageBytes)
    }
}
