package com.nexable.smartcookly.feature.fridge.presentation.add

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.focus.onFocusChanged
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
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.number
import org.jetbrains.compose.resources.painterResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_fruits
import smartcookly.composeapp.generated.resources.ic_next
import smartcookly.composeapp.generated.resources.ic_prev

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
                    onValueChange = { /* Not editable - only opens date picker */ },
                    readOnly = true,
                    enabled = !uiState.isLoading,
                    placeholder = { 
                        Text(
                            "Select expiration date",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        IconButton(
                            onClick = { 
                                if (!uiState.isLoading) {
                                    showDatePicker = true
                                }
                            },
                            enabled = !uiState.isLoading
                        ) {
                            Text(
                                "📅",
                                fontSize = 22.sp
                            )
                        }
                    },
                    trailingIcon = {
                        if (uiState.expirationDate != null) {
                            IconButton(
                                onClick = { onExpirationDateChange(null) },
                                enabled = !uiState.isLoading
                            ) {
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
                        .onFocusChanged { focusState ->
                            // Open date picker when field gains focus
                            if (focusState.isFocused && !uiState.isLoading && !showDatePicker) {
                                showDatePicker = true
                            }
                        }
                        .clickable(
                            enabled = !uiState.isLoading,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { 
                            if (!uiState.isLoading) {
                                showDatePicker = true
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
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
        // Ensure initialDate is always at least tomorrow
        val initialDate = uiState.expirationDate?.let { existingDate ->
            if (existingDate.toEpochDays() >= tomorrow.toEpochDays()) {
                existingDate
            } else {
                tomorrow
            }
        } ?: tomorrow
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                onExpirationDateChange(selectedDate)
                showDatePicker = false
            },
            initialDate = initialDate,
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
    var currentMonth by remember { mutableStateOf(initialDate) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(initialDate) }
    
    // Update selectedDate when initialDate changes
    LaunchedEffect(initialDate) {
        selectedDate = initialDate
        currentMonth = initialDate
    }
    
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    
    // Calculate calendar grid (6 weeks * 7 days = 42 cells)
    val calendarDays = remember(currentMonth, selectedDate, minDate) {
        val days = mutableListOf<Pair<Int, LocalDate?>>() // day number and date (null for prev/next month)
        
        // Get first day of current month
        val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.monthNumber, 1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek
        
        // Calculate days in month - get last day of the month
        val daysInMonth = try {
            // Get first day of next month, then subtract 1 day to get last day of current month
            val nextMonthFirstDay = firstDayOfMonth.plus(1, DateTimeUnit.MONTH)
            val lastDayOfMonth = nextMonthFirstDay.plus(-1, DateTimeUnit.DAY)
            lastDayOfMonth.dayOfMonth
        } catch (e: Exception) {
            // Fallback: use a safe default based on month
            when (currentMonth.monthNumber) {
                1, 3, 5, 7, 8, 10, 12 -> 31
                4, 6, 9, 11 -> 30
                2 -> if (currentMonth.year % 4 == 0 && (currentMonth.year % 100 != 0 || currentMonth.year % 400 == 0)) 29 else 28
                else -> 31
            }
        }
        
        // Calculate start offset (which day of week the 1st falls on)
        // isoDayNumber: Monday = 1, Tuesday = 2, ..., Sunday = 7
        val startOffset = (firstDayOfWeek.isoDayNumber - 1) % 7
        
        // Add previous month's trailing days (as placeholders)
        for (i in 0 until startOffset) {
            days.add(Pair(0, null)) // 0 indicates placeholder
        }
        
        // Add current month's days
        for (day in 1..daysInMonth) {
            val date = try {
                LocalDate(currentMonth.year, currentMonth.monthNumber, day)
            } catch (e: Exception) {
                null
            }
            days.add(Pair(day, date))
        }
        
        // Fill remaining cells to make 42 total (next month's leading days)
        while (days.size < 42) {
            days.add(Pair(0, null)) // 0 indicates placeholder
        }
        
        days
    }
    
    // Month name
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val monthName = monthNames.getOrNull(currentMonth.monthNumber - 1) ?: ""
    val yearText = currentMonth.year.toString()
    
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            // Header with month/year and navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentMonth = try {
                                currentMonth.plus(-1, DateTimeUnit.MONTH)
                            } catch (e: Exception) {
                                currentMonth
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_prev),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = ""
                        )
                    }
                    
                    Text(
                        text = "$monthName $yearText",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = {
                            currentMonth = try {
                                currentMonth.plus(1, DateTimeUnit.MONTH)
                            } catch (e: Exception) {
                                currentMonth
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_next),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = ""
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Day of week labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { dayLabel ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Calendar grid
                calendarDays.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        week.forEach { (dayNumber, date) ->
                            val isPlaceholder = dayNumber == 0
                            
                            // Reconstruct date if it's null but we have a day number (current month)
                            val actualDate = date ?: if (!isPlaceholder) {
                                try {
                                    LocalDate(currentMonth.year, currentMonth.monthNumber, dayNumber)
                                } catch (e: Exception) {
                                    null
                                }
                            } else {
                                null
                            }
                            
                            val isSelectable = actualDate?.let { 
                                it.toEpochDays() >= minDate.toEpochDays() 
                            } ?: false
                            val isSelected = actualDate?.let {
                                selectedDate?.let { sel ->
                                    sel.year == it.year && sel.monthNumber == it.monthNumber && sel.dayOfMonth == it.dayOfMonth
                                } ?: false
                            } ?: false
                            val isToday = actualDate?.let {
                                it.year == today.year && it.monthNumber == today.monthNumber && it.dayOfMonth == today.dayOfMonth
                            } ?: false
                            
                            if (isPlaceholder) {
                                // Empty cell for prev/next month days
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                )
                            } else {
                                // Always show the day number for current month days
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .then(
                                            if (isSelectable) {
                                                Modifier.clickable {
                                                    actualDate?.let { selectedDate = it }
                                                }
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayNumber.toString(),
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = dayNumber.toString(),
                                            color = when {
                                                !isSelectable -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                isToday -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // CANCEL button
                TextButton(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        "CANCEL",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                
                // OK button
                TextButton(
                    onClick = {
                        selectedDate?.let {
                            onDateSelected(it)
                        }
                    },
                    enabled = selectedDate != null,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        "OK",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp
    )
}

