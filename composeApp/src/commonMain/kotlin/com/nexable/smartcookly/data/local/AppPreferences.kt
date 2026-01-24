package com.nexable.smartcookly.data.local

import com.russhwolf.settings.Settings

class AppPreferences(private val settings: Settings) {
    fun isOnboardingCompleted(): Boolean = 
        settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    
    fun setOnboardingCompleted(completed: Boolean) {
        settings.putBoolean(KEY_ONBOARDING_COMPLETED, completed)
    }
    
    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
