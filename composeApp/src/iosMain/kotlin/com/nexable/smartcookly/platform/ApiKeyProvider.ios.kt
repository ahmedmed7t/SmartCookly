package com.nexable.smartcookly.platform

import platform.Foundation.NSBundle

actual fun getOpenAIApiKey(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("OPENAI_API_KEY") as? String ?: ""
}

actual fun getPexelsApiKey(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("PEXELS_API_KEY") as? String ?: ""
}
