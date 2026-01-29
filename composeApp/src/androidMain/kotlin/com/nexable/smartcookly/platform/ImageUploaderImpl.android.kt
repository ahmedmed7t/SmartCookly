package com.nexable.smartcookly.platform

import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

actual class ImageUploaderImpl actual constructor() : ImageUploader {
    override suspend fun uploadJpegBytes(userId: String, bytes: ByteArray, path: String): String {
        val ref = com.google.firebase.Firebase.storage.reference.child(path)
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }
}
