package com.nexable.smartcookly.feature.fridge.presentation.add

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_fruits

// Primary color constant
private val PrimaryGreen = Color(0xFF16664A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientContent(
    uiState: AddIngredientUiState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (FoodCategory) -> Unit,
    onExpirationDateChange: (LocalDate?) -> Unit,
    onSaveClick: () -> Unit,
    isModifyMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Enhanced Header Section with Gradient
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PrimaryGreen,
                                PrimaryGreen.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
            
            // Decorative circles
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-30).dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 20.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .shadow(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_fruits),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = when {
                        isModifyMode -> "Modify ingredient details"
                        uiState.isEditMode -> "Update your ingredient details"
                        else -> "Add a new ingredient to your fridge"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Form Content with better spacing
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Ingredient Name Field with Icon
            FormCard(
                title = "Ingredient Name",
                icon = "✏️"
            ) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    placeholder = { 
                        Text(
                            "e.g., Tomatoes, Milk, Chicken...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    enabled = !uiState.isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }

            // Category Selection with Icon
            FormCard(
                title = "Category",
                icon = "📂"
            ) {
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = !showCategoryDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.category?.let { category ->
                            "${category.emoji} ${category.displayName.lowercase().replaceFirstChar { it.uppercaseChar() }}"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { 
                            Text(
                                "Select a category",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                    ) {
                        FoodCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = category.emoji,
                                            fontSize = 22.sp
                                        )
                                        Text(
                                            text = category.displayName.lowercase().replaceFirstChar { it.uppercaseChar() },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                },
                                onClick = {
                                    onCategoryChange(category)
                                    showCategoryDropdown = false
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Expiration Date Section with Icon
            FormCard(
                title = "Expiration Date",
                subtitle = "Optional",
                icon = "📅"
            ) {
                OutlinedTextField(
                    value = uiState.expirationDate?.let { date ->
                        "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                    } ?: "",
                    onValueChange = { /* Not editable */ },
                    readOnly = true,
                    placeholder = { 
                        Text(
                            "Select expiration date",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Text(
                                "📅",
                                fontSize = 22.sp
                            )
                        }
                    },
                    trailingIcon = {
                        if (uiState.expirationDate != null) {
                            IconButton(onClick = { onExpirationDateChange(null) }) {
                                Text(
                                    "✕",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showDatePicker = true },
                    shape = RoundedCornerShape(14.dp),
                    enabled = !uiState.isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Enhanced Save/Update/Modify Button
            val buttonEnabled = !uiState.isLoading && uiState.name.isNotBlank() && uiState.category != null
            val buttonElevation by animateFloatAsState(
                targetValue = if (buttonEnabled) 4f else 0f,
                animationSpec = tween(300),
                label = "buttonElevation"
            )
            
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(buttonElevation.dp, RoundedCornerShape(18.dp)),
                enabled = buttonEnabled,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (uiState.isLoading && !isModifyMode) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            when {
                                isModifyMode -> "Modify Ingredient"
                                uiState.isEditMode -> "Update Ingredient"
                                else -> "Save Ingredient"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
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
                onExpirationDateChange(selectedDate)
                showDatePicker = false
            },
            initialDate = uiState.expirationDate ?: tomorrow,
            minDate = tomorrow
        )
    }
}

@Composable
private fun FormCard(
    title: String,
    subtitle: String? = null,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 18.sp
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            content()
        }
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
            val daysUntil = (selectedDate.toEpochDays() - minDate.toEpochDays())

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
        title = { 
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📅",
                    fontSize = 24.sp
                )
                Text(
                    "Select Expiration Date",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (dateError != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = dateError!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
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
                    isError = dateError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        isError = dateError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
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
                        isError = dateError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedDate = validateDate()
                    if (selectedDate != null) {
                        onDateSelected(selectedDate)
                    }
                },
                enabled = dateError == null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    )
}
