package com.nexable.smartcookly.platform

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have a system back button, so this is a no-op
    // The back button in the UI handles navigation
}
