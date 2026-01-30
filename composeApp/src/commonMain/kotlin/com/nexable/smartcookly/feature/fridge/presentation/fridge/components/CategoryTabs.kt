package com.nexable.smartcookly.feature.fridge.presentation.fridge.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory

private val PrimaryGreen = Color(0xFF16664A)

@Composable
fun CategoryTabs(
    selectedCategory: FoodCategory?,
    onCategorySelected: (FoodCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // All Items Tab
        item {
            CategoryTab(
                emoji = "🍽️",
                label = "All",
                isSelected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }

        // Category Tabs
        items(FoodCategory.entries.toList()) { category ->
            CategoryTab(
                emoji = category.emoji,
                label = category.displayName,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun CategoryTab(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = if (!isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        } else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}