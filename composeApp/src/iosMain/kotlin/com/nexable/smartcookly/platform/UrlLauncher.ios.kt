package com.nexable.smartcookly.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openUrlPlatform(context: Any?, url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}
