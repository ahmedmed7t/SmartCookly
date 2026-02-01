package com.nexable.smartcookly.feature.recipes.presentation.discover.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val PrimaryGreen = Color(0xFF16664A)
private val LightGreen = Color(0xFF2D8B6A)

private val cookingTips = listOf(
    "Finding the perfect recipes for you...",
    "Checking what's in your fridge...",
    "Matching your taste preferences...",
    "Discovering culinary delights...",
    "Almost there, preparing your menu...",
    "Cooking up some great ideas...",
    "Searching through cuisines...",
    "Finding dishes you'll love..."
)

private val cookingEmojis = listOf("👨‍🍳", "🍳", "🥘", "🍲", "🥗", "🍝", "🍜", "🥡")

@Composable
fun RecipeLoadingPlaceholder(
    modifier: Modifier = Modifier
) {
    var currentTipIndex by remember { mutableStateOf(0) }
    var currentEmojiIndex by remember { mutableStateOf(0) }
    
    // Rotate tips every 2.5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            currentTipIndex = (currentTipIndex + 1) % cookingTips.size
        }
    }
    
    // Rotate emojis every 2 seconds (slower for better UX)
    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            currentEmojiIndex = (currentEmojiIndex + 1) % cookingEmojis.size
        }
    }
    
    // Pulsing animation for the chef emoji
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Animated cooking icon section
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(PrimaryGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cookingEmojis[currentEmojiIndex],
                fontSize = (48 * scale).sp
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Loading message with animation (fixed height for two lines)
        Text(
            text = cookingTips[currentTipIndex],
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(48.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress indicator
        LinearProgressIndicator(
            modifier = Modifier
                .width(200.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryGreen,
            trackColor = PrimaryGreen.copy(alpha = 0.2f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Shimmer placeholder cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(3) { index ->
                ShimmerRecipeCard(
                    delayMillis = index * 150
                )
            }
        }
    }
}

@Composable
private fun ShimmerRecipeCard(
    delayMillis: Int = 0,
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing,
                delayMillis = delayMillis
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Image placeholder with overlays simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(brush)
            ) {
                // Cuisine badge placeholder (top-left)
                Spacer(
                    modifier = Modifier
                        .padding(12.dp)
                        .width(60.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                )
            }
            
            // Content section
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title placeholder
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                
                // Info badges row placeholder
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .width(70.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(55.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(45.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Button placeholder
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(brush)
                )
            }
        }
    }
}
