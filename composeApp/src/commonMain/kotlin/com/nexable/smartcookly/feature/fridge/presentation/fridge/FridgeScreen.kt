package com.nexable.smartcookly.feature.fridge.presentation.fridge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.presentation.fridge.components.*
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToReviewScan: (String) -> Unit,
    viewModel: FridgeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddBottomSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Fridge",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${uiState.totalItemCount} ITEMS IN STOCK",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
//                    IconButton(onClick = { /* TODO: Search */ }) {
//                        Icon(Icons.Default.Search, contentDescription = "Search")
//                    }
//                    IconButton(onClick = { /* TODO: Filter */ }) {
//                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
//                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("+ Add Item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            CategoryTabs(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) },
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.selectedCategory == null) {
                    // Show grouped items by category
                    uiState.groupedItems.forEach { (category, items) ->
                        item {
                            CategoryHeader(
                                category = category,
                                itemCount = items.size,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        
                        items(items) { item ->
                            FridgeItemCard(item = item)
                        }
                    }
                } else {
                    // Show items for selected category
                    items(uiState.items) { item ->
                        FridgeItemCard(item = item)
                    }
                }
                
                if (uiState.items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Your fridge is empty",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Tap the + button to add items",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showAddBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.background,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .padding(vertical = 12.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
            }
        ) {
            AddItemBottomSheet(
                onCameraClick = onNavigateToCamera,
                onManualAddClick = { /* TODO: Manual add */ },
                onDismiss = { showAddBottomSheet = false }
            )
        }
    }
}
