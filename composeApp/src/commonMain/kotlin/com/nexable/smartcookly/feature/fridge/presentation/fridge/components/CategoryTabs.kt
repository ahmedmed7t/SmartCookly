package com.nexable.smartcookly.feature.fridge.presentation.fridge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory

@Composable
fun CategoryTabs(
    selectedCategory: FoodCategory?,
    onCategorySelected: (FoodCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        item {
            CategoryTab(
                label = "All Items",
                isSelected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FoodCategory.entries.forEach { category ->
                CategoryTab(
                    label = category.name.replaceFirstChar { it.uppercase() },
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

        }
    }
}

@Composable
private fun CategoryTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
