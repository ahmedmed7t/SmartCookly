package com.nexable.smartcookly.feature.onboarding.presentation.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title with emoji
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🥗", fontSize = 28.sp)
            Text(
                text = "Your Eating Style",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "We'll tailor your recipe discovery and fridge alerts to your preferences.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dietary style list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DietaryStyle.entries) { style ->
                SelectableDietaryStyleCard(
                    dietaryStyle = style,
                    isSelected = selectedDietaryStyle == style,
                    onClick = { onDietaryStyleSelect(style) }
                )
            }

            // Other text field
            if (showOtherTextField) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otherDietaryStyleText,
                        onValueChange = onOtherTextChange,
                        label = { Text("Specify other dietary style") },
                        placeholder = { Text("e.g., Gluten-free, Paleo...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
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
