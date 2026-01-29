package com.nexable.smartcookly.platform

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.StorageReference
import dev.gitlive.firebase.storage.UploadTask

actual class ImageUploaderImpl actual constructor() : ImageUploader {
    private val storage: FirebaseStorage = Firebase.storage

    override suspend fun uploadJpegBytes(
        userId: String,
        bytes: ByteArray,
        path: String
    ): String {
        val ref: StorageReference = storage.reference.child(path)
        // Upload bytes - putBytes() returns UploadTask which needs to be awaited
        val uploadTask: UploadTask = ref.putBytes(bytes)
        uploadTask.await() // Wait for upload to complete
        // Get download URL after upload completes
        return ref.getDownloadUrl()
    }
}
