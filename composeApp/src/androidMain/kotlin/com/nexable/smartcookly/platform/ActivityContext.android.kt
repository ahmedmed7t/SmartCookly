package com.nexable.smartcookly.platform

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getActivityContext(): Any? {
    val context = LocalContext.current
    return context as? Activity
}
