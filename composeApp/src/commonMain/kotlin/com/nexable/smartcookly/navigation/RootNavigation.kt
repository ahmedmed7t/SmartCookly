package com.nexable.smartcookly.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.auth.presentation.LoginScreen
import com.nexable.smartcookly.feature.auth.presentation.SignUpScreen
import com.nexable.smartcookly.feature.favorites.presentation.FavoritesScreen
import com.nexable.smartcookly.feature.fridge.presentation.add.AddIngredientScreen
import com.nexable.smartcookly.feature.onboarding.presentation.LoginEncouragementScreen
import com.nexable.smartcookly.feature.onboarding.presentation.OnboardingScreen
import com.nexable.smartcookly.feature.profile.presentation.ProfileScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.EditAllergiesScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.EditCookingLevelScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.EditCuisinesScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.EditDietaryScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.EditDislikedScreen
import com.nexable.smartcookly.feature.profile.presentation.edit.EditHealthScreen
import com.nexable.smartcookly.feature.recipes.presentation.cooking.CookingModeScreen
import com.nexable.smartcookly.feature.recipes.presentation.discover.DiscoverRecipesScreen
import com.nexable.smartcookly.feature.recipes.presentation.discover.RecipeDetailsScreen
import com.nexable.smartcookly.feature.shopping.presentation.add.AddShoppingItemScreen
import com.nexable.smartcookly.feature.subscription.presentation.CustomerCenterScreen
import com.nexable.smartcookly.feature.subscription.presentation.PaywallScreen
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

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
                    onNavigateToDiscoverRecipes = {
                        navController.navigate(Screen.DiscoverRecipes.route)
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    },
                    onNavigateToAddShoppingItem = {
                        navController.navigate(Screen.AddShoppingItem.route)
                    },
                    onNavigateToQuickMeals = {
                        navController.navigate(Screen.DiscoverRecipes.route)
                    },
                    onNavigateToPaywall = {
                        navController.navigate(Screen.Paywall.route)
                    },
                    fridgeRefreshKey = fridgeRefreshKey
                )
            }
            
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onViewRecipe = { recipe ->
                        FavoriteRecipeCache.storeRecipe(recipe)
                        navController.navigate(Screen.FavoriteRecipeFlow.route)
                    }
                )
            }
            
            composable(Screen.FavoriteRecipeFlow.route) {
                FavoriteRecipeFlowScreen(
                    onNavigateBack = {
                        FavoriteRecipeCache.clearRecipe()
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        FavoriteRecipeCache.clearRecipe()
                        navController.navigate(Screen.App.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
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
                    onManageSubscription = {
                        navController.navigate(Screen.CustomerCenter.route)
                    },
                    onLogout = {
                        scope.launch {
                            authRepository.signOut()
                            // Logout from RevenueCat (revert to anonymous)
                            try {
                                Purchases.sharedInstance.awaitLogOut()
                            } catch (e: Exception) {
                                println("RevenueCat logout failed: ${e.message}")
                            }
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
            
            composable(Screen.AddShoppingItem.route) {
                AddShoppingItemScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.DiscoverRecipes.route) {
                DiscoverRecipesScreen(
                    onNavigateBack = {
                        DiscoveryParamsCache.clearParams()
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        DiscoveryParamsCache.clearParams()
                        navController.navigate(Screen.App.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable(Screen.Paywall.route) {
                PaywallScreen(
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.CustomerCenter.route) {
                CustomerCenterScreen(
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteRecipeFlowScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val navController = rememberNavController()
    val recipe = FavoriteRecipeCache.getRecipe()
    var isCompletionScreenShowing by remember { mutableStateOf(false) }
    
    // Determine toolbar title based on current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val toolbarTitle = when {
        currentRoute?.startsWith("cooking_mode") == true -> {
            recipe?.let { "Cooking: ${it.name}" } ?: "Cooking"
        }
        currentRoute?.startsWith("recipe_details") == true -> {
            recipe?.name ?: "Recipe Details"
        }
        else -> "Recipe"
    }
    
    Scaffold(
        topBar = {
            // Hide toolbar when completion screen is showing
            if (!isCompletionScreenShowing) {
                TopAppBar(
                    title = {
                        Text(
                            text = toolbarTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                // Check if we can pop back in the NavController
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                } else {
                                    // We're at the root, navigate back
                                    onNavigateBack()
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(Res.drawable.ic_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (recipe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Recipe not found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }
        
        NavHost(
            navController = navController,
            startDestination = Screen.DiscoverRecipesSubScreen.RecipeDetails.createRoute(recipe.id),
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = Screen.DiscoverRecipesSubScreen.RecipeDetails.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { _ ->
                RecipeDetailsScreen(
                    recipe = recipe,
                    onStartCooking = {
                        navController.navigate(
                            Screen.DiscoverRecipesSubScreen.CookingMode.createRoute(recipe.id)
                        )
                    },
                    showFavoriteButton = false, // Hide favorite button for favorites flow
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            composable(
                route = Screen.DiscoverRecipesSubScreen.CookingMode.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { _ ->
                CookingModeScreen(
                    recipe = recipe,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToHome = onNavigateToHome,
                    onCompletionScreenVisibilityChanged = { isShowing ->
                        isCompletionScreenShowing = isShowing
                    },
                    showAddToFavorites = false // Hide add to favorites button for favorites flow
                )
            }
        }
    }
}
