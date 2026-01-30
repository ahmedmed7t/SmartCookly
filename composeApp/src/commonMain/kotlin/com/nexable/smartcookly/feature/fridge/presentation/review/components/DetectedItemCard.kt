package com.nexable.smartcookly.feature.fridge.presentation.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_pen
import smartcookly.composeapp.generated.resources.ic_trash

// Primary color constant
private val PrimaryGreen = Color(0xFF16664A)

@Composable
fun DetectedItemCard(
    item: FridgeItem,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysUntilExpiration = item.expirationDate?.let { date ->
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        (date.toEpochDays() - today.toEpochDays())
    }

    val isExpiringSoon =
        daysUntilExpiration != null && daysUntilExpiration <= 2 && daysUntilExpiration >= 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpiringSoon) {
                Color(0xFFFFF3E0).copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Emoji Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        color = getCategoryColor(item.category).copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.category.emoji,
                    fontSize = 24.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                item.expirationDate?.let { date ->
                    Text(
                        text = "Expires ${date.dayOfMonth}/${date.monthNumber}/${date.year}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isExpiringSoon) {
                    Text(
                        text = "⚠ EXPIRING IN ${daysUntilExpiration} DAYS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFF57C00)
                    )
                }

                Text(
                    text = item.category.displayName.lowercase().replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit Button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_pen),
                        contentDescription = "Edit item",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Remove Button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_trash),
                        contentDescription = "Remove item",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
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
