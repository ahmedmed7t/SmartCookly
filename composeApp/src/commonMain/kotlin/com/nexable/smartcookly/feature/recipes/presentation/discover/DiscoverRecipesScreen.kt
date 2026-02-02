package com.nexable.smartcookly.feature.recipes.presentation.discover

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.presentation.DiscoveryMode
import com.nexable.smartcookly.feature.recipes.presentation.cooking.CookingModeScreen
import com.nexable.smartcookly.navigation.DiscoveryParamsCache
import com.nexable.smartcookly.navigation.Screen
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverRecipesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    viewModel: DiscoverRecipesViewModel = koinInject()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var isCompletionScreenShowing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Load discovery parameters from cache
    val params = DiscoveryParamsCache.getParams()
    
    // Show snackbar when favorite is added
    LaunchedEffect(uiState.favoriteAddedRecipeId) {
        uiState.favoriteAddedRecipeId?.let { recipeId ->
            // Check if we're currently viewing this recipe
            val currentRecipeId = navBackStackEntry?.arguments?.getString("recipeId")
            if (currentRecipeId == recipeId || uiState.recipes.any { it.id == recipeId }) {
                snackbarHostState.showSnackbar(
                    message = "Recipe Added to Favorites",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    
    // Show snackbar when there's an error adding to favorites
    LaunchedEffect(uiState.favoriteError) {
        uiState.favoriteError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }
    
    LaunchedEffect(params) {
        if (params != null) {
            viewModel.loadRecipes(
                discoveryMode = params.discoveryMode,
                cuisines = params.cuisines
            )
        }
    }
    
    // Determine toolbar title based on current route
    val toolbarTitle = when {
        currentRoute?.startsWith("cooking_mode") == true -> {
            val recipeId = navBackStackEntry?.arguments?.getString("recipeId")
            val recipe = uiState.recipes.find { it.id == recipeId } ?: uiState.selectedRecipe
            recipe?.let { "Cooking: ${it.name}" } ?: "Cooking"
        }
        currentRoute?.startsWith("recipe_details") == true -> {
            val recipeId = navBackStackEntry?.arguments?.getString("recipeId")
            val recipe = uiState.recipes.find { it.id == recipeId } ?: uiState.selectedRecipe
            recipe?.name ?: "Recipe Details"
        }
        else -> "Discover Recipes"
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
                                    // We're at the root, navigate back to Recipes screen
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.DiscoverRecipesSubScreen.DiscoverRecipesList.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.DiscoverRecipesSubScreen.DiscoverRecipesList.route) {
                DiscoverRecipesListScreen(
                    uiState = uiState,
                    onRecipeClick = { recipe ->
                        viewModel.selectRecipe(recipe)
                        navController.navigate(
                            Screen.DiscoverRecipesSubScreen.RecipeDetails.createRoute(recipe.id)
                        )
                    },
                    onRetry = {
                        params?.let {
                            viewModel.retry()
                            viewModel.loadRecipes(
                                discoveryMode = it.discoveryMode,
                                cuisines = it.cuisines
                            )
                        }
                    }
                )
            }
            
            composable(
                route = Screen.DiscoverRecipesSubScreen.RecipeDetails.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                val recipe = uiState.recipes.find { it.id == recipeId } ?: uiState.selectedRecipe
                
                RecipeDetailsScreen(
                    recipe = recipe,
                    onStartCooking = {
                        navController.navigate(
                            Screen.DiscoverRecipesSubScreen.CookingMode.createRoute(recipe?.id.orEmpty())
                        )
                    },
                    isAddingFavorite = uiState.isAddingFavorite && uiState.addingFavoriteRecipeId == recipeId,
                    isFavorited = recipeId in uiState.favoritedRecipeIds,
                    onAddToFavorites = {
                        recipe?.let { viewModel.addToFavorites(it) }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            composable(
                route = Screen.DiscoverRecipesSubScreen.CookingMode.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                val recipe = uiState.recipes.find { it.id == recipeId } ?: uiState.selectedRecipe
                
                if (recipe != null) {
                    CookingModeScreen(
                        recipe = recipe,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToHome = onNavigateToHome,
                        onCompletionScreenVisibilityChanged = { isShowing ->
                            isCompletionScreenShowing = isShowing
                        }
                    )
                }
            }
        }
    }
}
