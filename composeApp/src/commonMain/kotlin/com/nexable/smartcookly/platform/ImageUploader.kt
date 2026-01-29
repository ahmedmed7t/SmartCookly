package com.nexable.smartcookly.platform

interface ImageUploader {
    suspend fun uploadJpegBytes(
        userId: String,
        bytes: ByteArray,
        path: String = "users/$userId/scan_image.jpg"
    ): String // returns downloadUrl
}

expect class ImageUploaderImpl() : ImageUploader
