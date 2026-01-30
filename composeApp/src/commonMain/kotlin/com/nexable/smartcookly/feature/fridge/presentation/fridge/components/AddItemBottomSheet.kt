package com.nexable.smartcookly.feature.fridge.presentation.fridge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_next

private val PrimaryGreen = Color(0xFF16664A)

@Composable
fun AddItemBottomSheet(
    onCameraClick: () -> Unit,
    onManualAddClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Add to Your Fridge",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose how you'd like to add items",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Camera Option - Featured
        AddOptionCard(
            emoji = "📸",
            title = "Scan with Camera",
            description = "AI-powered instant detection",
            isHighlighted = true,
            onClick = {
                onCameraClick()
                onDismiss()
            }
        )

        // Manual Option
        AddOptionCard(
            emoji = "✏️",
            title = "Add Manually",
            description = "Enter ingredient details yourself",
            isHighlighted = false,
            onClick = {
                onManualAddClick()
                onDismiss()
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AddOptionCard(
    emoji: String,
    title: String,
    description: String,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isHighlighted) PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = if (isHighlighted) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isHighlighted) Color.White.copy(alpha = 0.2f) 
                        else PrimaryGreen.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isHighlighted) Color.White else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isHighlighted) Color.White.copy(alpha = 0.85f) 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isHighlighted) Color.White.copy(alpha = 0.2f) 
                        else PrimaryGreen.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_next),
                    contentDescription = "Go",
                    tint = if (isHighlighted) Color.White else PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
