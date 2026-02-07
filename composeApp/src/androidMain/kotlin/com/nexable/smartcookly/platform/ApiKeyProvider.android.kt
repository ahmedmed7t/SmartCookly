package com.nexable.smartcookly.platform

import com.nexable.smartcookly.BuildConfig

actual fun getOpenAIApiKey(): String {
    return BuildConfig.OPENAI_API_KEY
}

actual fun getPexelsApiKey(): String {
    return BuildConfig.PEXELS_API_KEY
}
