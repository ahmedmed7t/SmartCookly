package com.nexable.smartcookly.platform

actual fun getOpenAIApiKey(): String {
    // In production, load from Info.plist or secure storage
    // For now, return the provided key
    return "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"
}

actual fun getPexelsApiKey(): String {
    // In production, load from Info.plist or secure storage
    return "j66UEJjKOdJZ8kJsaGtRSXVmdeioxG42wy82bLFviRy2fAgweX4e5Kc6"
}
