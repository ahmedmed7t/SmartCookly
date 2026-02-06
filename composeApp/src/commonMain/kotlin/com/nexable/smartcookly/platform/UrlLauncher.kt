package com.nexable.smartcookly.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// Composable helper function that returns a lambda to open URL
@Composable
fun rememberOpenUrl(): (String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { url: String ->
            openUrlPlatform(context, url)
        }
    }
}

// Platform-specific implementation
expect fun openUrlPlatform(context: Any?, url: String)
