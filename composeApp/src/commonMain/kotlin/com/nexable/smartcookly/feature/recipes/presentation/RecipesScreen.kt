package com.nexable.smartcookly.feature.recipes.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.recipes.presentation.components.*
import org.koin.compose.koinInject

private val PrimaryGreen = Color(0xFF16664A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.isBottomSheetVisible) {
        if (uiState.isBottomSheetVisible) {
            try {
                sheetState.show()
            } catch (e: Exception) {
                // Sheet already shown or dismissed
            }
        } else {
            try {
                sheetState.hide()
            } catch (e: Exception) {
                // Sheet already hidden
            }
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && uiState.isBottomSheetVisible) {
            viewModel.hideBottomSheet()
        }
    }

    Scaffold(
        bottomBar = {
            Column {
                Spacer(modifier = Modifier.height(2.dp))
                Button(
                    onClick = { viewModel.discoverRecipes() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 32.dp),
                    enabled = !uiState.isLoading && canDiscoverRecipes(uiState),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Discover Recipes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "🍴",
                            fontSize = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Screen Title
                Text(
                    text = "Find Your Next Meal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Discovery Mode Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Discovery Mode",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "How should we find your recipes?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    DiscoveryModeCard(
                        icon = "🔍",
                        title = "My Preferences",
                        description = "Based on your taste profiles",
                        isSelected = uiState.discoveryMode == DiscoveryMode.PREFERENCES,
                        onClick = { viewModel.selectDiscoveryMode(DiscoveryMode.PREFERENCES) }
                    )

                    DiscoveryModeCard(
                        icon = "🧊",
                        title = "My Fridge",
                        description = "Using ingredients you have",
                        isSelected = uiState.discoveryMode == DiscoveryMode.FRIDGE,
                        onClick = { viewModel.selectDiscoveryMode(DiscoveryMode.FRIDGE) }
                    )

                    DiscoveryModeCard(
                        icon = "✨",
                        title = "Both",
                        description = "The perfect combination",
                        isSelected = uiState.discoveryMode == DiscoveryMode.BOTH,
                        onClick = { viewModel.selectDiscoveryMode(DiscoveryMode.BOTH) }
                    )
                }

                // Cuisine Context Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Cuisine Context",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Which cuisines to explore?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CuisineContextCard(
                            icon = "❤️",
                            title = "My Favorites",
                            isSelected = uiState.cuisineContext == CuisineContext.FAVORITES,
                            onClick = { viewModel.selectCuisineContext(CuisineContext.FAVORITES) },
                            modifier = Modifier.weight(1f)
                        )

                        CuisineContextCard(
                            icon = "🌍",
                            title = "Select Others",
                            isSelected = uiState.cuisineContext == CuisineContext.SELECT_OTHERS,
                            onClick = {
                                viewModel.selectCuisineContext(CuisineContext.SELECT_OTHERS)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Cuisine Display Section
                when (uiState.cuisineContext) {
                    CuisineContext.FAVORITES -> {
                        if (uiState.favoriteCuisines.isNotEmpty()) {
                            CuisineDisplaySection(
                                cuisines = uiState.favoriteCuisines,
                                showEditButton = false,
                                onEditClick = { }
                            )
                        }
                    }

                    CuisineContext.SELECT_OTHERS -> {
                        CuisineDisplaySection(
                            cuisines = uiState.selectedOtherCuisines,
                            showEditButton = true,
                            onEditClick = { viewModel.showBottomSheet() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Discover Recipes Button
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Sheet
        if (uiState.isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.hideBottomSheet() },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                CuisineSelectionBottomSheet(
                    selectedCuisines = uiState.selectedOtherCuisines,
                    onCuisineToggle = { cuisine ->
                        val current = uiState.selectedOtherCuisines.toMutableSet()
                        if (current.contains(cuisine)) {
                            current.remove(cuisine)
                        } else {
                            current.add(cuisine)
                        }
                        viewModel.updateSelectedCuisines(current)
                    },
                    onDone = { viewModel.hideBottomSheet() }
                )
            }
        }
    }
}

private fun canDiscoverRecipes(uiState: RecipesUiState): Boolean {
    return when (uiState.cuisineContext) {
        CuisineContext.FAVORITES -> uiState.favoriteCuisines.isNotEmpty()
        CuisineContext.SELECT_OTHERS -> uiState.selectedOtherCuisines.isNotEmpty()
    }
}
