package com.nexable.smartcookly.feature.fridge.presentation.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.presentation.review.ReviewScanViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    onNavigateBack: () -> Unit,
    editItem: FridgeItem? = null,
    reviewScanViewModel: ReviewScanViewModel? = null,
    viewModel: AddIngredientViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load item data when editing
    LaunchedEffect(editItem) {
        editItem?.let { item ->
            viewModel.loadItem(item)
        }
    }

    // Determine if we're in modify mode (working with ReviewScanViewModel)
    val isModifyMode = reviewScanViewModel != null && editItem != null
    
    // Show success snackbar when save succeeds
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess && !isModifyMode) {
            val message = if (uiState.isEditMode) {
                "Ingredient updated successfully"
            } else {
                "Ingredient added successfully"
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessFlag()
            onNavigateBack()
        }
    }

    // Show error snackbar when there's an error
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isModifyMode -> "Modify Ingredient"
                            uiState.isEditMode -> "Edit Ingredient"
                            else -> "Add Ingredient"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        AddIngredientContent(
            uiState = uiState,
            onNameChange = { viewModel.updateName(it) },
            onCategoryChange = { viewModel.updateCategory(it) },
            onExpirationDateChange = { viewModel.updateExpirationDate(it) },
            onSaveClick = {
                if (isModifyMode && reviewScanViewModel != null && editItem != null) {
                    // Modify mode: update item in ReviewScanViewModel
                    val updatedItem = FridgeItem(
                        id = editItem.id,
                        name = uiState.name.trim(),
                        category = uiState.category!!,
                        expirationDate = uiState.expirationDate,
                        imageUrl = editItem.imageUrl
                    )
                    reviewScanViewModel.updateItem(updatedItem)
                    onNavigateBack()
                } else {
                    // Normal mode: save to Firestore
                    viewModel.saveIngredient()
                }
            },
            isModifyMode = isModifyMode,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

