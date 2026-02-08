package com.nexable.smartcookly.feature.onboarding.presentation.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableIngredientCard
import org.jetbrains.compose.resources.painterResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_check

@Composable
fun IngredientsAvoidanceStep(
    avoidedIngredients: Set<Ingredient>,
    otherIngredientText: String,
    showOtherTextField: Boolean,
    onIngredientToggle: (Ingredient) -> Unit,
    onOtherTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title with emoji
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "⚠️", fontSize = 28.sp)
            Text(
                text = "Any allergies?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Select any allergens to avoid. We'll filter out recipes containing these ingredients.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )

        // Selection count badge
        if (avoidedIngredients.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "${avoidedIngredients.size} allergen${if (avoidedIngredients.size > 1) "s" else ""} selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ingredient grid
        val ingredientsWithoutNothing = Ingredient.entries.filter { it != Ingredient.NOTHING }
        val rows = ingredientsWithoutNothing.chunked(2)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nothing option - displayed prominently at the top
            item {
                val isNothingSelected = avoidedIngredients.contains(Ingredient.NOTHING)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIngredientToggle(Ingredient.NOTHING) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNothingSelected) 
                            MaterialTheme.colorScheme.errorContainer 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        width = if (isNothingSelected) 2.dp else 1.dp,
                        color = if (isNothingSelected) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Ingredient.NOTHING.emoji,
                                fontSize = 28.sp
                            )
                            Column {
                                Text(
                                    text = Ingredient.NOTHING.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "I don't have any allergies",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (isNothingSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_check),
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { ingredient ->
                        Box(modifier = Modifier.weight(1f)) {
                            SelectableIngredientCard(
                                ingredient = ingredient,
                                isSelected = avoidedIngredients.contains(ingredient),
                                onClick = { onIngredientToggle(ingredient) }
                            )
                        }
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Other text field
            if (showOtherTextField) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otherIngredientText,
                        onValueChange = onOtherTextChange,
                        label = { Text("Specify other allergen") },
                        placeholder = { Text("e.g., Latex, Nickel...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
