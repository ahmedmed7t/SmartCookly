package com.nexable.smartcookly.feature.fridge.presentation.fridge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.fridge.data.model.FreshStatus
import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_trash

// Color Constants
private val PrimaryGreen = Color(0xFF16664A)
private val FreshBlue = Color(0xFF1976D2)
private val FreshBlueBg = Color(0xFFE3F2FD)
private val GoodGreen = Color(0xFF388E3C)
private val GoodGreenBg = Color(0xFFE8F5E9)
private val UrgentOrange = Color(0xFFF57C00)
private val UrgentOrangeBg = Color(0xFFFFF3E0)
private val ExpiredRed = Color(0xFFD32F2F)
private val ExpiredRedBg = Color(0xFFFFEBEE)

@Composable
fun FridgeItemCard(
    item: FridgeItem,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = item.calculateFreshStatus()
    val statusColors = getStatusColors(status)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    .size(52.dp)
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

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Name and Delete Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_trash),
                            contentDescription = "Delete item",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Expiration info and Status Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Expiration Text with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Status indicator dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColors.textColor)
                        )
                        
                        Text(
                            text = getExpirationText(item),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Status Badge
                    StatusBadge(status = status)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: FreshStatus,
    modifier: Modifier = Modifier
) {
    val colors = getStatusColors(status)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = colors.backgroundColor
    ) {
        Text(
            text = colors.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = colors.textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

private data class StatusColors(
    val backgroundColor: Color,
    val textColor: Color,
    val label: String
)

private fun getStatusColors(status: FreshStatus): StatusColors {
    return when (status) {
        FreshStatus.FRESH -> StatusColors(FreshBlueBg, FreshBlue, "FRESH")
        FreshStatus.GOOD -> StatusColors(GoodGreenBg, GoodGreen, "GOOD")
        FreshStatus.URGENT -> StatusColors(UrgentOrangeBg, UrgentOrange, "USE SOON")
        FreshStatus.EXPIRED -> StatusColors(ExpiredRedBg, ExpiredRed, "EXPIRED")
    }
}

private fun getExpirationText(item: FridgeItem): String {
    return item.expirationDate?.let { date ->
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysUntil = (date.toEpochDays() - today.toEpochDays()).toInt()
        when {
            daysUntil < 0 -> "Expired ${-daysUntil} days ago"
            daysUntil == 0 -> "Expires today"
            daysUntil == 1 -> "Expires tomorrow"
            daysUntil <= 7 -> "Fresh for $daysUntil days"
            else -> "Expires ${date.dayOfMonth}/${date.monthNumber}"
        }
    } ?: "No expiration set"
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
