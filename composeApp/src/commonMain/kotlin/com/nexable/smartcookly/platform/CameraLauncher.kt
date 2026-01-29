package com.nexable.smartcookly.platform

import androidx.compose.runtime.Composable

expect class CameraLauncher(context: Any?) {
    @Composable
    fun rememberCameraLauncher(
        onImageCaptured: (ByteArray) -> Unit,
        onError: (String) -> Unit
    ): CameraLauncherHandle?
}

expect class CameraLauncherHandle {
    fun launch()
}
