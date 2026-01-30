package com.nexable.smartcookly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.nexable.smartcookly.feature.profile.presentation.ProfileScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.*
import com.nexable.smartcookly.feature.fridge.presentation.add.AddIngredientScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val appPreferences: AppPreferences = koinInject()
    val authRepository: AuthRepository = koinInject()
    val scope = rememberCoroutineScope()
    var profileRefreshKey by remember { mutableStateOf(0) }
    var fridgeRefreshKey by remember { mutableStateOf(0) }
    
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
                AppNavigation(
                    onLogout = {
                        // Navigate to login and clear entire back stack
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToAddIngredient = {
                        EditItemCache.clearEditItem()
                        navController.navigate(Screen.AddIngredient.route)
                    },
                    onNavigateToEditIngredient = { item ->
                        EditItemCache.storeEditItem(item)
                        navController.navigate(Screen.AddIngredient.route)
                    },
                    fridgeRefreshKey = fridgeRefreshKey,
                    onFridgeRefresh = {
                        fridgeRefreshKey++
                    }
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onEditCuisines = {
                        navController.navigate(Screen.EditCuisines.route)
                    },
                    onEditDietary = {
                        navController.navigate(Screen.EditDietary.route)
                    },
                    onEditAllergies = {
                        navController.navigate(Screen.EditAllergies.route)
                    },
                    onEditDisliked = {
                        navController.navigate(Screen.EditDisliked.route)
                    },
                    onEditHealth = {
                        navController.navigate(Screen.EditHealth.route)
                    },
                    onEditCookingLevel = {
                        navController.navigate(Screen.EditCookingLevel.route)
                    },
                    onLogout = {
                        scope.launch {
                            authRepository.signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    refreshKey = profileRefreshKey
                )
            }
            
            composable(Screen.EditCuisines.route) {
                EditCuisinesScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        profileRefreshKey++
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.EditDietary.route) {
                EditDietaryScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        profileRefreshKey++
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.EditAllergies.route) {
                EditAllergiesScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        profileRefreshKey++
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.EditDisliked.route) {
                EditDislikedScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        profileRefreshKey++
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.EditHealth.route) {
                EditHealthScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        profileRefreshKey++
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.EditCookingLevel.route) {
                EditCookingLevelScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        profileRefreshKey++
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.AddIngredient.route) {
                val editItem = EditItemCache.getEditItem()
                AddIngredientScreen(
                    onNavigateBack = {
                        EditItemCache.clearEditItem()
                        fridgeRefreshKey++
                        navController.popBackStack()
                    },
                    editItem = editItem
                )
            }
        }
    }
}
