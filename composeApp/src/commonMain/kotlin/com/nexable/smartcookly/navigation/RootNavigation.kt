package com.nexable.smartcookly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.presentation.LoginScreen
import com.nexable.smartcookly.feature.onboarding.data.OnboardingDataCache
import com.nexable.smartcookly.feature.onboarding.presentation.OnboardingScreen
import org.koin.compose.koinInject

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val appPreferences: AppPreferences = koinInject()
    
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        startDestination = if (appPreferences.isOnboardingCompleted()) {
            Screen.Login.route
        } else {
            Screen.Onboarding.route
        }
    }
    
    startDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        // Clear onboarding cache
                        OnboardingDataCache.clear()
                        // Mark onboarding as completed
                        appPreferences.setOnboardingCompleted(true)
                        // Navigate to login
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen()
            }
        }
    }
}
