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
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.auth.presentation.LoginScreen
import com.nexable.smartcookly.feature.auth.presentation.SignUpScreen
import com.nexable.smartcookly.feature.onboarding.presentation.LoginEncouragementScreen
import com.nexable.smartcookly.feature.onboarding.presentation.OnboardingScreen
import org.koin.compose.koinInject

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val appPreferences: AppPreferences = koinInject()
    val authRepository: AuthRepository = koinInject()
    
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        // Check if user is already authenticated (Firebase persists sessions automatically)
        val currentUser = authRepository.getCurrentUser()
        startDestination = when {
            currentUser != null -> {
                // User is logged in, go to home
                Screen.App.route
            }
            appPreferences.isGuestMode() -> {
                // User chose guest mode previously, go to home
                Screen.App.route
            }
            appPreferences.isOnboardingCompleted() -> {
                // Onboarding completed but not logged in, show login
                Screen.Login.route
            }
            else -> {
                // First time user, show onboarding
                Screen.Onboarding.route
            }
        }
    }
    
    startDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = destination
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        // Navigate to login encouragement screen
                        // Don't pop onboarding so user can go back to final step
                        navController.navigate(Screen.LoginEncouragement.route)
                    }
                )
            }
            
            composable(Screen.LoginEncouragement.route) {
                LoginEncouragementScreen(
                    onLoginClick = {
                        // Mark onboarding as completed
                        appPreferences.setOnboardingCompleted(true)
                        // Navigate to login and clear entire back stack
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    onBackClick = {
                        // Pop back to onboarding (will restore to step 5)
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // Clear guest mode when user logs in
                        appPreferences.setGuestMode(false)
                        // Navigate to app and clear entire back stack
                        navController.navigate(Screen.App.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate(Screen.SignUp.route)
                    },
                    onContinueAsGuestClick = {
                        // Persist guest mode preference
                        appPreferences.setGuestMode(true)
                        // Navigate to app and clear entire back stack
                        navController.navigate(Screen.App.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = {
                        // Clear guest mode when user signs up
                        appPreferences.setGuestMode(false)
                        // Navigate to app and clear entire back stack
                        navController.navigate(Screen.App.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.App.route) {
                AppNavigation()
            }
        }
    }
}
