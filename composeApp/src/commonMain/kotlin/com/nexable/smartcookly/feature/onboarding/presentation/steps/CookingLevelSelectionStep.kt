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
import com.nexable.smartcookly.feature.onboarding.data.model.CookingLevel
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableCookingLevelCard

@Composable
fun CookingLevelSelectionStep(
    selectedCookingLevel: CookingLevel?,
    onCookingLevelSelect: (CookingLevel) -> Unit,
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
            Text(text = "👨‍🍳", fontSize = 28.sp)
            Text(
                text = "Your Cooking Level",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle
        Text(
            text = "We'll adjust recipe complexity and cooking tips to match your skills.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Info card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "💡", fontSize = 20.sp)
                Text(
                    text = "This helps us suggest recipes with the right difficulty level and provide helpful cooking tips.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cooking level list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(CookingLevel.entries) { level ->
                SelectableCookingLevelCard(
                    cookingLevel = level,
                    isSelected = selectedCookingLevel == level,
                    onClick = { onCookingLevelSelect(level) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
