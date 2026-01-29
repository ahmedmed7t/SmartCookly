package com.nexable.smartcookly.platform

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

actual class CameraLauncherHandle(
    private val permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    private val cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    private val uri: Uri,
    private val activity: ComponentActivity,
    private val onError: (String) -> Unit
) {
    actual fun launch() {
        // Check if permission is granted
        val hasPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            cameraLauncher.launch(uri)
        } else {
            // Request permission first
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            cameraLauncher.launch(uri)
        } else {
            onError("Camera permission denied")
        }
    }
}

actual class CameraLauncher actual constructor(context: Any?) {
    private val activity: ComponentActivity? = context as? ComponentActivity
    
    @SuppressLint("ContextCastToActivity")
    @Composable
    actual fun rememberCameraLauncher(
        onImageCaptured: (ByteArray) -> Unit,
        onError: (String) -> Unit
    ): CameraLauncherHandle? {
        val currentActivity = activity ?: (LocalContext.current as? ComponentActivity)
        
        return if (currentActivity != null) {
            val tempFile = remember {
                File(currentActivity.cacheDir, "camera_temp_${System.currentTimeMillis()}.jpg")
            }
            
            val uri = remember {
                FileProvider.getUriForFile(
                    currentActivity,
                    "${currentActivity.packageName}.fileprovider",
                    tempFile
                )
            }
            
            // Camera launcher - defined first
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success ->
                if (success && tempFile.exists()) {
                    try {
                        // Read the image file
                        val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                        bitmap?.let {
                            // Resize bitmap if too large to reduce payload size
                            val maxDimension = 1024 // Max width or height
                            val scaledBitmap = if (it.width > maxDimension || it.height > maxDimension) {
                                val scale = maxDimension.toFloat() / maxOf(it.width, it.height)
                                val newWidth = (it.width * scale).toInt()
                                val newHeight = (it.height * scale).toInt()
                                Bitmap.createScaledBitmap(it, newWidth, newHeight, true)
                            } else {
                                it
                            }
                            
                            // Compress to JPEG bytes (reduced quality for smaller size)
                            val outputStream = ByteArrayOutputStream()
                            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                            val imageBytes = outputStream.toByteArray()
                            
                            // Clean up scaled bitmap if different from original
                            if (scaledBitmap != it) {
                                scaledBitmap.recycle()
                            }
                            
                            onImageCaptured(imageBytes)
                        } ?: onError("Failed to decode image")
                        
                        // Clean up temp file
                        tempFile.delete()
                    } catch (e: Exception) {
                        onError("Error processing image: ${e.message}")
                        tempFile.delete()
                    }
                } else {
                    onError("Failed to capture image")
                    if (tempFile.exists()) tempFile.delete()
                }
            }
            
            // Permission launcher - references cameraLauncher
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    cameraLauncher.launch(uri)
                } else {
                    onError("Camera permission denied. Please grant camera permission in settings.")
                }
            }
            
            CameraLauncherHandle(permissionLauncher, cameraLauncher, uri, currentActivity, onError)
        } else {
            null
        }
    }
}
