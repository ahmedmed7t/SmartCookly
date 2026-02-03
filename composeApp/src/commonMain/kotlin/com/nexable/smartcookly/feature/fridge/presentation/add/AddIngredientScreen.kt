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
    
    // Track if we should navigate back after showing snackbar
    var shouldNavigateBack by remember { mutableStateOf(false) }
    var navigateBackMessage by remember { mutableStateOf<String?>(null) }
    
    // Handle success events from SharedFlow
    LaunchedEffect(Unit) {
        viewModel.saveSuccessEvent.collect { event ->
            when (event) {
                is SaveSuccessEvent.Added -> {
                    // In add mode, just show message and stay on screen
                    snackbarHostState.showSnackbar("Ingredient added successfully")
                    // Form is already cleared by ViewModel, stay on screen to allow adding more ingredients
                }
                is SaveSuccessEvent.Updated -> {
                    // In edit mode, navigate back after update
                    snackbarHostState.showSnackbar("Ingredient updated successfully")
                    kotlinx.coroutines.delay(200) // Wait for snackbar to show
                    onNavigateBack()
                }
            }
        }
    }
    
    // Handle navigation back for modify mode after showing snackbar
    LaunchedEffect(shouldNavigateBack, navigateBackMessage) {
        if (shouldNavigateBack && navigateBackMessage != null) {
            snackbarHostState.showSnackbar(navigateBackMessage!!)
            kotlinx.coroutines.delay(200) // Wait for snackbar to show
            onNavigateBack()
            shouldNavigateBack = false
            navigateBackMessage = null
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
                    // Show success message and navigate back
                    shouldNavigateBack = true
                    navigateBackMessage = "Ingredient updated successfully"
                } else {
                    // Normal mode: save to Firestore (handles both add and edit modes)
                    viewModel.saveIngredient()
                }
            },
            isModifyMode = isModifyMode,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

