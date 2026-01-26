package com.nexable.smartcookly.feature.onboarding.presentation.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.onboarding.data.model.CookingLevel
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableCookingLevelCard
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun CookingLevelSelectionStep(
    selectedCookingLevel: CookingLevel?,
    onCookingLevelSelect: (CookingLevel) -> Unit,
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
            text = "Your Cooking Level",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle
        Text(
            text = "We'll adjust recipe complexity and cooking tips to match your skills.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Cooking level list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            items(CookingLevel.entries) { level ->
                SelectableCookingLevelCard(
                    cookingLevel = level,
                    isSelected = selectedCookingLevel == level,
                    onClick = { onCookingLevelSelect(level) }
                )
            }
        }
    }
}
