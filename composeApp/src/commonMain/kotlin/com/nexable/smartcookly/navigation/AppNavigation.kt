package com.nexable.smartcookly.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.nexable.smartcookly.feature.profile.presentation.edit.*
import com.nexable.smartcookly.feature.recipes.presentation.RecipesScreen
import com.nexable.smartcookly.platform.CameraLauncher
import com.nexable.smartcookly.platform.getActivityContext
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
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAddIngredient: () -> Unit = {},
    onNavigateToEditIngredient: (com.nexable.smartcookly.feature.fridge.data.model.FridgeItem) -> Unit = {},
    onNavigateToDiscoverRecipes: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    fridgeRefreshKey: Int = 0,
    onFridgeRefresh: () -> Unit = {}
) {
    val navController = rememberNavController()
    val authRepository: AuthRepository = koinInject()
    val scope = rememberCoroutineScope()
    var profileRefreshKey by remember { mutableStateOf(0) }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isReviewScanScreen = currentDestination?.route == Screen.ReviewScan.route
    
    Scaffold(
        bottomBar = {
            // Hide bottom navigation bar for ReviewScanScreen
            if (!isReviewScanScreen) {
                NavigationBar(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    val bottomNavItems = listOf(
                        BottomNavItem(Screen.Home, "HOME", Res.drawable.ic_home),
                        BottomNavItem(Screen.Fridge, "FRIDGE", Res.drawable.ic_fridge),
                        BottomNavItem(Screen.Recipes, "RECIPES", Res.drawable.ic_ingredient),
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(
                if (isReviewScanScreen) PaddingValues(0.dp) else paddingValues
            )
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onScanFridgeClick = {
                        // Navigate to fridge screen or camera
                        navController.navigate(Screen.Fridge.route)
                    },
                    onStartCookingClick = {
                        // Navigate to Recipes screen (tab 3)
                        navController.navigate(Screen.Recipes.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onProfileClick = {
                        onNavigateToProfile()
                    },
                    onFavoritesClick = {
                        onNavigateToFavorites()
                    }
                )
            }
            composable(Screen.Fridge.route) {
                key(fridgeRefreshKey) {
                    val activityContext = getActivityContext()
                    val cameraLauncher = remember(activityContext) {
                        CameraLauncher(activityContext)
                    }
                    
                    var cameraError by remember { mutableStateOf<String?>(null) }
                    
                    val cameraHandle = cameraLauncher.rememberCameraLauncher(
                        onImageCaptured = { imageBytes ->
                            ImageCache.storeImageBytes(imageBytes)
                            navController.navigate(Screen.ReviewScan.route)
                        },
                        onError = { error ->
                            cameraError = error
                            println("Camera error: $error")
                        }
                    )
                    
                    FridgeScreen(
                        onNavigateToCamera = {
                            cameraHandle?.launch()
                        },
                        onNavigateToAddIngredient = {
                            onNavigateToAddIngredient()
                        },
                        onNavigateToEditIngredient = { item ->
                            onNavigateToEditIngredient(item)
                        },
                        cameraError = cameraError,
                        onCameraErrorDismissed = { cameraError = null },
                        refreshKey = fridgeRefreshKey
                    )
                }
            }
            
            composable(Screen.Recipes.route) {
                com.nexable.smartcookly.feature.recipes.presentation.RecipesScreen(
                    onNavigateToDiscoverRecipes = onNavigateToDiscoverRecipes
                )
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
                val imageBytes = ImageCache.getImageBytes()
                if (imageBytes != null) {
                    ReviewScanScreen(
                        imageBytes = imageBytes,
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
}

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: DrawableResource
)
