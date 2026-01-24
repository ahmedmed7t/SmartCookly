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
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableDietaryStyleCard

@Composable
fun DietaryStyleSelectionStep(
    selectedDietaryStyle: DietaryStyle?,
    otherDietaryStyleText: String,
    showOtherTextField: Boolean,
    onDietaryStyleSelect: (DietaryStyle) -> Unit,
    onOtherTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        // Title
        Text(
            text = "Your Eating Style",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle
        Text(
            text = "We'll tailor your recipe discovery and fridge alerts to your preferences.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Dietary style list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            items(DietaryStyle.entries) { style ->
                SelectableDietaryStyleCard(
                    dietaryStyle = style,
                    isSelected = selectedDietaryStyle == style,
                    onClick = { onDietaryStyleSelect(style) }
                )
            }

            item {
                // Other text field (shown when Other is selected)
                if (showOtherTextField) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otherDietaryStyleText,
                        onValueChange = onOtherTextChange,
                        label = { Text("Specify other dietary style") },
                        placeholder = { Text("e.g., Gluten-free, Paleo...") },
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
