package com.nexable.smartcookly.feature.fridge.presentation.fridge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun CategoryHeader(
    category: FoodCategory,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Emoji badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getCategoryColor(category).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.emoji,
                    fontSize = 16.sp
                )
            }
            
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Item count badge
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = PrimaryGreen.copy(alpha = 0.1f)
        ) {
            Text(
                text = "$itemCount items",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = PrimaryGreen,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}


private fun getCategoryColor(category: FoodCategory): Color {
    return when (category) {
        FoodCategory.DAIRY -> Color(0xFF90CAF9)
        FoodCategory.VEGETABLES -> Color(0xFF81C784)
        FoodCategory.FRUITS -> Color(0xFFFFAB91)
        FoodCategory.MEAT -> Color(0xFFEF9A9A)
        FoodCategory.SEAFOOD -> Color(0xFF80DEEA)
        FoodCategory.GRAINS -> Color(0xFFFFCC80)
        FoodCategory.BEVERAGES -> Color(0xFFCE93D8)
        FoodCategory.CONDIMENTS -> Color(0xFFA5D6A7)
        FoodCategory.SNACKS -> Color(0xFFFFE082)
        FoodCategory.FROZEN -> Color(0xFF81D4FA)
        FoodCategory.OTHER -> Color(0xFFB0BEC5)
    }
}
