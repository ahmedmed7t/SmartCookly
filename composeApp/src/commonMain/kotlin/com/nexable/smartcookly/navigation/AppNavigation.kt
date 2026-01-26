package com.nexable.smartcookly.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.fridge.presentation.fridge.FridgeScreen
import com.nexable.smartcookly.feature.fridge.presentation.review.ReviewScanScreen
import com.nexable.smartcookly.feature.home.presentation.HomeScreen
import com.nexable.smartcookly.feature.profile.presentation.ProfileScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_fridge
import smartcookly.composeapp.generated.resources.ic_home
import smartcookly.composeapp.generated.resources.ic_ingredient
import smartcookly.composeapp.generated.resources.ic_shopping_cart

@Composable
fun AppNavigation(
    onLogout: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val navController = rememberNavController()
    val authRepository: AuthRepository = koinInject()
    val scope = rememberCoroutineScope()
    var profileRefreshKey by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                val bottomNavItems = listOf(
                    BottomNavItem(Screen.Home, "HOME", Res.drawable.ic_home),
                    BottomNavItem(Screen.Fridge, "FRIDGE", Res.drawable.ic_fridge),
                    BottomNavItem(Screen.Favorites, "FAVORITES", Res.drawable.ic_ingredient),
                    BottomNavItem(Screen.Shopping, "SHOPPING", Res.drawable.ic_shopping_cart),
                )
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(22.dp),
                                painter = painterResource(item.icon),
                                contentDescription = ""
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onScanFridgeClick = {
                        // Navigate to fridge screen or camera
                        navController.navigate(Screen.Fridge.route)
                    },
                    onProfileClick = {
                        onNavigateToProfile()
                    }
                )
            }
            composable(Screen.Fridge.route) {
                FridgeScreen(
                    onNavigateToCamera = {
                        // TODO open system camera app to pick only one image
                    },
                    onNavigateToReviewScan = { imageBase64 ->
                        ImageCache.storeImage(imageBase64)
                        navController.navigate(Screen.ReviewScan.route)
                    }
                )
            }
            
            composable(Screen.Favorites.route) {
                // Placeholder for Recipes screen
                Text("Recipes Screen - Coming Soon")
            }
            
            composable(Screen.Shopping.route) {
                // Placeholder for Shopping List screen
                Text("Shopping List Screen - Coming Soon")
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

            composable(Screen.ReviewScan.route) {
                val imageBase64 = ImageCache.getImage() ?: ""
                ReviewScanScreen(
                    imageBase64 = imageBase64,
                    onNavigateBack = {
                        ImageCache.clearImage()
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        ImageCache.clearImage()
                        navController.popBackStack(Screen.Fridge.route, inclusive = false)
                    }
                )
            }
        }
    }
}

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: DrawableResource
)
