package com.nexable.smartcookly.feature.shopping.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nexable.smartcookly.feature.shopping.data.model.Urgency

private val PrimaryGreen = Color(0xFF16664A)
private val WarmOrange = Color(0xFFFF9500)
private val UrgentRed = Color(0xFFE74C3C)

@Composable
fun AddToShoppingDialog(
    initialIngredientName: String = "",
    onAdd: (String, Urgency) -> Unit,
    onDismiss: () -> Unit,
    isAdding: Boolean = false,
    error: String? = null
) {
    var ingredientName by remember { mutableStateOf(initialIngredientName) }
    var selectedUrgency by remember { mutableStateOf(Urgency.NORMAL) }
    
    // Reset ingredient name when initial value changes
    LaunchedEffect(initialIngredientName) {
        ingredientName = initialIngredientName
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PrimaryGreen.copy(alpha = 0.2f),
                                    PrimaryGreen.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛒",
                        fontSize = 40.sp
                    )
                }

                // Title
                Text(
                    text = "Add to Shopping List",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Ingredient Name Text Field
                OutlinedTextField(
                    value = ingredientName,
                    onValueChange = { 
                        ingredientName = it
                        // Clear error when user starts typing
                        if (error != null) {
                            // Error will be cleared by ViewModel on next add attempt
                        }
                    },
                    label = {
                        Text(
                            text = "Ingredient Name",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    placeholder = {
                        Text(
                            text = "e.g., Tomatoes, Onions...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        focusedLabelColor = PrimaryGreen,
                        errorBorderColor = UrgentRed,
                        errorLabelColor = UrgentRed
                    ),
                    enabled = !isAdding,
                    singleLine = true,
                    isError = error != null,
                    supportingText = if (error != null) {
                        { Text(text = error, color = UrgentRed) }
                    } else null
                )

                // Urgency Selector
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "How urgent is this?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UrgencyChip(
                            label = "Can Wait",
                            emoji = "⏰",
                            isSelected = selectedUrgency == Urgency.LOW,
                            onClick = { selectedUrgency = Urgency.LOW },
                            color = PrimaryGreen,
                            modifier = Modifier.weight(1f),
                            enabled = !isAdding
                        )
                        UrgencyChip(
                            label = "Need Soon",
                            emoji = "📋",
                            isSelected = selectedUrgency == Urgency.NORMAL,
                            onClick = { selectedUrgency = Urgency.NORMAL },
                            color = WarmOrange,
                            modifier = Modifier.weight(1f),
                            enabled = !isAdding
                        )
                        UrgencyChip(
                            label = "Urgent",
                            emoji = "🔥",
                            isSelected = selectedUrgency == Urgency.HIGH,
                            onClick = { selectedUrgency = Urgency.HIGH },
                            color = UrgentRed,
                            modifier = Modifier.weight(1f),
                            enabled = !isAdding
                        )
                    }
                }

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        enabled = !isAdding,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }

                    // Add Button
                    Button(
                        onClick = {
                            if (ingredientName.isNotBlank()) {
                                onAdd(ingredientName.trim(), selectedUrgency)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        enabled = !isAdding && ingredientName.isNotBlank(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Add",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Text("✓", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UrgencyChip(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) {
            color.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        enabled = enabled
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
