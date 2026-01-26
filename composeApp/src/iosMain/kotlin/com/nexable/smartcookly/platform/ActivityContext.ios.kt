package com.nexable.smartcookly.platform

import androidx.compose.runtime.Composable

@Composable
actual fun getActivityContext(): Any? {
    // iOS doesn't have Activity concept, return null
    return null
}
