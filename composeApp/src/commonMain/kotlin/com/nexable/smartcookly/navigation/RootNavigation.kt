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
import com.nexable.smartcookly.feature.auth.presentation.SignUpScreen
import com.nexable.smartcookly.feature.onboarding.presentation.LoginEncouragementScreen
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
