package com.nexable.smartcookly.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSData
import platform.UIKit.*
import platform.darwin.NSObject
import kotlinx.cinterop.ObjCObject
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.addressOf

@ObjCObject
class ImagePickerDelegate(
    private val onImageCaptured: (ByteArray) -> Unit,
    private val onError: (String) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol {
    
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        // Use UIImagePickerControllerOriginalImage or UIImagePickerControllerEditedImage
        val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] 
            ?: didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage]) as? UIImage
        if (image != null) {
            try {
                // Resize image if too large to reduce payload size (max 1024px on longest side)
                val maxDimension = 1024.0
                val imageSize = image.size
                val width = imageSize.useContents { width }
                val height = imageSize.useContents { height }
                val maxSize = maxOf(width, height)
                
                val resizedImage = if (maxSize > maxDimension) {
                    val scale = maxDimension / maxSize
                    val newWidth = width * scale
                    val newHeight = height * scale
                    val newSize = platform.Foundation.CGSizeMake(newWidth, newHeight)
                    UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
                    image.drawInRect(platform.Foundation.CGRectMake(0.0, 0.0, newWidth, newHeight))
                    val resized = UIGraphicsGetImageFromCurrentImageContext()
                    UIGraphicsEndImageContext()
                    resized ?: image
                } else {
                    image
                }
                
                // Convert UIImage to JPEG data (reduced quality 0.7 for smaller size)
                val jpegData = UIImageJPEGRepresentation(resizedImage, 0.7)
                if (jpegData != null) {
                    // Convert NSData to ByteArray
                    val length = jpegData.length.toInt()
                    val bytes = ByteArray(length)
                    jpegData.bytes.usePinned { pinned ->
                        val sourcePtr = pinned.addressOf(0).reinterpret<ByteVar>()
                        for (i in 0 until length) {
                            bytes[i] = sourcePtr[i]
                        }
                    }
                    onImageCaptured(bytes)
                } else {
                    onError("Failed to convert image to JPEG")
                }
            } catch (e: Exception) {
                onError("Error processing image: ${e.message}")
            }
        } else {
            onError("No image selected")
        }
        
        picker.dismissViewControllerAnimated(true, completion = null)
    }
    
    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        onError("Camera cancelled")
    }
}

actual class CameraLauncherHandle(
    private val viewController: UIViewController?,
    private val picker: UIImagePickerController,
    private val delegate: ImagePickerDelegate
) : CameraLauncherHandle {
    override fun launch() {
        viewController?.presentViewController(picker, animated = true, completion = null)
    }
}

actual class CameraLauncher(context: Any?) {
    private val viewController: UIViewController? = context as? UIViewController
    
    @Composable
    actual fun rememberCameraLauncher(
        onImageCaptured: (ByteArray) -> Unit,
        onError: (String) -> Unit
    ): CameraLauncherHandle? {
        val currentViewController = remember {
            viewController ?: getCurrentViewController()
        }
        
        return if (currentViewController != null) {
            val delegate = remember {
                ImagePickerDelegate(onImageCaptured, onError)
            }
            
            val picker = remember {
                UIImagePickerController().apply {
                    sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    allowsEditing = false
                    delegate = delegate
                }
            }
            
            CameraLauncherHandle(currentViewController, picker, delegate)
        } else {
            null
        }
    }
    
    private fun getCurrentViewController(): UIViewController? {
        // Try to get the root view controller from the key window or scenes
        val app = UIApplication.sharedApplication
        val window = app.keyWindow ?: app.windows.firstOrNull()
        return window?.rootViewController
    }
}
