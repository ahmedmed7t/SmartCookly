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
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableCuisineCard

@Composable
fun CuisineSelectionStep(
    selectedCuisines: Set<Cuisine>,
    otherCuisineText: String,
    showOtherTextField: Boolean,
    onCuisineToggle: (Cuisine) -> Unit,
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
            text = "What do you love to eat?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle
        Text(
            text = "Select your favorite cuisines so our AI can personalize your recipe recommendations.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Cuisine grid using LazyColumn with rows
        val cuisines = Cuisine.values().toList()
        val rows = cuisines.chunked(2)

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
                    row.forEach { cuisine ->
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            SelectableCuisineCard(
                                cuisine = cuisine,
                                isSelected = selectedCuisines.contains(cuisine),
                                onClick = { onCuisineToggle(cuisine) }
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
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otherCuisineText,
                        onValueChange = onOtherTextChange,
                        label = { Text("Specify other cuisine") },
                        placeholder = { Text("e.g., Ethiopian, Peruvian...") },
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
