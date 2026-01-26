package com.nexable.smartcookly.feature.onboarding.presentation.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableDislikedIngredientCard

@Composable
fun DislikedIngredientsStep(
    dislikedIngredients: Set<DislikedIngredient>,
    otherDislikedIngredientText: String,
    showOtherTextField: Boolean,
    onIngredientToggle: (DislikedIngredient) -> Unit,
    onOtherTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        
        // Title
        Text(
            text = "Anything off the menu?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle
        Text(
            text = "Select ingredients you don't like. We'll avoid recipes with these ingredients.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Ingredient grid using LazyColumn with rows
        val rows = DislikedIngredient.entries.chunked(2)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            items(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { ingredient ->
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            SelectableDislikedIngredientCard(
                                ingredient = ingredient,
                                isSelected = dislikedIngredients.contains(ingredient),
                                onClick = { onIngredientToggle(ingredient) }
                            )
                        }
                    }
                    // Add spacer if odd number of items
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                // Other text field (shown when Other is selected)
                if (showOtherTextField) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otherDislikedIngredientText,
                        onValueChange = onOtherTextChange,
                        label = { Text("Specify other ingredient") },
                        placeholder = { Text("e.g., Cinnamon, Cumin...") },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
