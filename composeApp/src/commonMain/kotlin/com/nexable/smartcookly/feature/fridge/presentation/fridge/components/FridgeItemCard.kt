package com.nexable.smartcookly.feature.fridge.presentation.fridge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.fridge.data.model.FreshStatus
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_trash

@Composable
fun FridgeItemCard(
    item: FridgeItem,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Delete icon button in top right corner
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_trash),
                    contentDescription = "Delete item",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder for image - would use AsyncImage in real implementation
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        val quantityText = buildString {
                            item.expirationDate?.let { date ->
                                val today = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                val daysUntil = (date.toEpochDays() - today.toEpochDays()).toInt()
                                when {
                                    daysUntil < 0 -> append("Expired ${-daysUntil} days ago")
                                    daysUntil == 0 -> append("Expires today")
                                    daysUntil == 1 -> append("Expires tomorrow")
                                    daysUntil <= 5 -> append("Fresh for $daysUntil days")
                                    else -> append("Expires ${date.dayOfMonth}/${date.monthNumber}")
                                }
                            } ?: run {
                                append("No expiration date")
                            }
                        }
                        
                        // Expiration text and status badge on the same line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = quantityText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            
                            StatusBadge(status = item.calculateFreshStatus())
                        }
                    }
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
    val (backgroundColor, textColor, label) = when (status) {
        FreshStatus.FRESH -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF1976D2),
            "FRESH"
        )
        FreshStatus.GOOD -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF388E3C),
            "GOOD"
        )
        FreshStatus.URGENT -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFF57C00),
            "URGENT"
        )
        FreshStatus.EXPIRED -> Triple(
            Color.Transparent,
            Color(0xFFD32F2F),
            "DISCARD"
        )
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}
