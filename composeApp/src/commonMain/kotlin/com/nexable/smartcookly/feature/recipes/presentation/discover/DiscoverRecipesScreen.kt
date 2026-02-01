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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.presentation.DiscoveryMode
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
    viewModel: DiscoverRecipesViewModel = koinInject()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    
    // Load discovery parameters from cache
    val params = DiscoveryParamsCache.getParams()
    
    LaunchedEffect(params) {
        if (params != null) {
            viewModel.loadRecipes(
                discoveryMode = params.discoveryMode,
                cuisines = params.cuisines
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discover Recipes",
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
