package com.nexable.smartcookly.feature.fridge.presentation.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddIngredientViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show success snackbar when save succeeds
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            snackbarHostState.showSnackbar("Ingredient added successfully")
            viewModel.clearSuccessFlag()
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
                        "Add Ingredient",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Ingredient Name Field
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Ingredient Name") },
                placeholder = { Text("e.g., Tomatoes, Milk, Chicken...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = showCategoryDropdown,
                onExpandedChange = { showCategoryDropdown = !showCategoryDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.category?.name?.replace("_", " ")?.lowercase()
                        ?.replaceFirstChar { it.uppercaseChar() } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    placeholder = { Text("Select a category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading
                )
                ExposedDropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    FoodCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category.name.replace("_", " ").lowercase()
                                        .replaceFirstChar { it.uppercaseChar() }
                                )
                            },
                            onClick = {
                                viewModel.updateCategory(category)
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            // Expiration Date Field (Optional)
            OutlinedTextField(
                value = uiState.expirationDate?.toString() ?: "",
                onValueChange = { /* Not editable */ },
                readOnly = true,
                label = { Text("Expiration Date (Optional)") },
                placeholder = { Text("Select expiration date") },
                trailingIcon = {
                    Text(
                        "📅",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                enabled = true,
            )

            // Clear date button if date is set
            if (uiState.expirationDate != null) {
                TextButton(
                    onClick = { viewModel.updateExpirationDate(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear expiration date")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = { viewModel.saveIngredient() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading && uiState.name.isNotBlank() && uiState.category != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        "Save",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                viewModel.updateExpirationDate(selectedDate)
                showDatePicker = false
            },
            initialDate = uiState.expirationDate ?: tomorrow,
            minDate = tomorrow
        )
    }
}

@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate,
    minDate: LocalDate
) {
    var year by remember { mutableStateOf(initialDate.year.toString()) }
    var month by remember { mutableStateOf(initialDate.monthNumber.toString()) }
    var day by remember { mutableStateOf(initialDate.dayOfMonth.toString()) }
    var dateError by remember { mutableStateOf<String?>(null) }

    fun validateDate(): LocalDate? {
        val y = year.toIntOrNull()
        val m = month.toIntOrNull()
        val d = day.toIntOrNull()

        if (y == null || m == null || d == null) {
            dateError = "Please enter valid numbers"
            return null
        }

        if (m !in 1..12) {
            dateError = "Month must be between 1 and 12"
            return null
        }

        if (d !in 1..31) {
            dateError = "Day must be between 1 and 31"
            return null
        }

        return try {
            val selectedDate = LocalDate(y, m, d)
            val daysUntil = (selectedDate.toEpochDays() - minDate.toEpochDays()).toInt()

            if (daysUntil < 0) {
                dateError = "Date must be from tomorrow onwards"
                null
            } else {
                dateError = null
                selectedDate
            }
        } catch (e: Exception) {
            dateError = "Invalid date"
            null
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Expiration Date") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (dateError != null) {
                    Text(
                        text = dateError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                OutlinedTextField(
                    value = year,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            year = it
                            dateError = null
                        }
                    },
                    label = { Text("Year") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = dateError != null
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = month,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 2) {
                                month = it
                                dateError = null
                            }
                        },
                        label = { Text("Month") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = dateError != null
                    )
                    OutlinedTextField(
                        value = day,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 2) {
                                day = it
                                dateError = null
                            }
                        },
                        label = { Text("Day") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = dateError != null
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDate = validateDate()
                    if (selectedDate != null) {
                        onDateSelected(selectedDate)
                    }
                },
                enabled = dateError == null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
