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
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DetectedItemCard(
    item: FridgeItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysUntilExpiration = item.expirationDate?.let { date ->
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        (date.toEpochDays() - today.toEpochDays()).toInt()
    }
    
    val isExpiringSoon = daysUntilExpiration != null && daysUntilExpiration <= 2 && daysUntilExpiration >= 0
    
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
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Placeholder for image
            Box(
                modifier = Modifier
                    .size(60.dp)
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
                
                item.category.let { category ->
                    Text(
                        text = "CATEGORY: ${category.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            TextButton(
                onClick = onRemove
            ) {
                Text(
                    text = "X",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
