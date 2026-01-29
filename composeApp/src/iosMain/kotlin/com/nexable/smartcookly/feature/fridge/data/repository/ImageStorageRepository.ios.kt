package com.nexable.smartcookly.feature.fridge.data.repository

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.create
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class ImageUploaderImpl actual constructor() : ImageUploader {
    override suspend fun uploadJpegBytes(userId: String, bytes: ByteArray, path: String): String {
        val storage = FirebaseStorage.storage() // حسب الـ wrapper عندك
        val ref = storage.reference().child(path)

        val data = bytes.toNSData()

        // upload
        suspendCancellableCoroutine<Unit> { cont ->
            ref.putData(data, metadata = null) { _, error ->
                if (error != null) cont.resumeWithException(Throwable(error.localizedDescription))
                else cont.resume(Unit)
            }
        }

        // download url
        return suspendCancellableCoroutine { cont ->
            ref.downloadURL { url, error ->
                if (error != null) cont.resumeWithException(Throwable(error.localizedDescription))
                else cont.resume(url!!.absoluteString)
            }
        }
    }
}

private fun ByteArray.toNSData(): NSData =
    NSData.create(bytes = this, length = this.size.toULong())