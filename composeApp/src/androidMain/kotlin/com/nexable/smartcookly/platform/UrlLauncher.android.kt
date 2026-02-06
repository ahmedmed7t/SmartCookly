package com.nexable.smartcookly.platform

import android.content.Context
import android.content.Intent
import android.net.Uri

actual fun openUrlPlatform(context: Any?, url: String) {
    val androidContext = context as? Context ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    androidContext.startActivity(intent)
}
