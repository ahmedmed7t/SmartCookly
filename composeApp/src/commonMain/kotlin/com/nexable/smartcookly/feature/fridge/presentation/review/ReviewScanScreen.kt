package com.nexable.smartcookly.feature.fridge.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.presentation.add.AddIngredientContent
import com.nexable.smartcookly.feature.fridge.presentation.add.AddIngredientViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScanScreen(
    imageBytes: ByteArray,
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: ReviewScanViewModel = koinInject(parameters = { parametersOf(imageBytes) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Determine TopAppBar title based on current route
    val topBarTitle = when {
        currentRoute?.startsWith("modify_ingredient") == true -> "Modify Ingredient"
        else -> "Review Scan"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topBarTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 19.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.currentBackStackEntry != null && navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Only show bottom bar when on list screen
            if (currentRoute == "review_scan_list") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.reviewedItems.isNotEmpty() || uiState.autoSavedItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "✔ ${uiState.reviewedItems.size} ITEMS REVIEWED · ${uiState.autoSavedItems.size} ITEMS AUTO-SAVED",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        
                        Button(
                            onClick = {
                                viewModel.saveToFridge()
                                onSaveComplete()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.detectedItems.isNotEmpty() && !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Save to Fridge")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "review_scan_list",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("review_scan_list") {
                ReviewScanListScreen(
                    viewModel = viewModel,
                    onNavigateToModify = { itemId ->
                        navController.navigate("modify_ingredient/$itemId")
                    }
                )
            }
            
            composable(
                route = "modify_ingredient/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                val item = uiState.detectedItems.find { it.id == itemId }
                
                if (item != null) {
                    // Create a local ViewModel instance for this modify flow
                    val modifyViewModel: AddIngredientViewModel = koinInject()
                    
                    // Load item data when entering modify screen
                    LaunchedEffect(item) {
                        modifyViewModel.loadItem(item)
                    }
                    
                    val modifyUiState by modifyViewModel.uiState.collectAsState()
                    
                    AddIngredientContent(
                        uiState = modifyUiState,
                        onNameChange = { modifyViewModel.updateName(it) },
                        onCategoryChange = { modifyViewModel.updateCategory(it) },
                        onExpirationDateChange = { modifyViewModel.updateExpirationDate(it) },
                        onSaveClick = {
                            // Update item in ReviewScanViewModel
                            val updatedItem = FridgeItem(
                                id = item.id,
                                name = modifyUiState.name.trim(),
                                category = modifyUiState.category!!,
                                expirationDate = modifyUiState.expirationDate,
                                imageUrl = item.imageUrl
                            )
                            viewModel.updateItem(updatedItem)
                            navController.popBackStack()
                        },
                        isModifyMode = true
                    )
                }
            }
        }
    }
}
