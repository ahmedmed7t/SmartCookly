package com.nexable.smartcookly.feature.home.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.fridge.data.model.FreshStatus
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.shopping.data.model.ShoppingItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val PrimaryGreen = Color(0xFF16664A)
private val WarmOrange = Color(0xFFFF9500)
private val UrgentRed = Color(0xFFE74C3C)

// Get appropriate emoji for ingredient
private fun getIngredientEmoji(ingredient: String): String {
    val lowerIngredient = ingredient.lowercase()
    return when {
        // Proteins
        lowerIngredient.contains("chicken") -> "🍗"
        lowerIngredient.contains("beef") || lowerIngredient.contains("steak") -> "🥩"
        lowerIngredient.contains("pork") || lowerIngredient.contains("bacon") -> "🥓"
        lowerIngredient.contains("fish") || lowerIngredient.contains("salmon") || lowerIngredient.contains("tuna") -> "🐟"
        lowerIngredient.contains("shrimp") || lowerIngredient.contains("prawn") -> "🦐"
        lowerIngredient.contains("egg") -> "🥚"
        
        // Dairy
        lowerIngredient.contains("milk") -> "🥛"
        lowerIngredient.contains("cheese") -> "🧀"
        lowerIngredient.contains("butter") -> "🧈"
        
        // Vegetables
        lowerIngredient.contains("tomato") -> "🍅"
        lowerIngredient.contains("carrot") -> "🥕"
        lowerIngredient.contains("onion") -> "🧅"
        lowerIngredient.contains("garlic") -> "🧄"
        lowerIngredient.contains("ginger") -> "🫚"
        lowerIngredient.contains("pepper") || lowerIngredient.contains("chili") -> "🌶️"
        lowerIngredient.contains("corn") -> "🌽"
        lowerIngredient.contains("broccoli") -> "🥦"
        lowerIngredient.contains("lettuce") || lowerIngredient.contains("salad") -> "🥬"
        lowerIngredient.contains("cucumber") -> "🥒"
        lowerIngredient.contains("potato") -> "🥔"
        lowerIngredient.contains("mushroom") -> "🍄"
        lowerIngredient.contains("avocado") -> "🥑"
        lowerIngredient.contains("eggplant") -> "🍆"
        
        // Fruits
        lowerIngredient.contains("apple") -> "🍎"
        lowerIngredient.contains("lemon") -> "🍋"
        lowerIngredient.contains("orange") -> "🍊"
        lowerIngredient.contains("banana") -> "🍌"
        lowerIngredient.contains("strawberry") -> "🍓"
        lowerIngredient.contains("grape") -> "🍇"
        lowerIngredient.contains("coconut") -> "🥥"
        
        // Grains & Bread
        lowerIngredient.contains("bread") -> "🍞"
        lowerIngredient.contains("rice") -> "🍚"
        lowerIngredient.contains("pasta") || lowerIngredient.contains("noodle") -> "🍝"
        
        // Condiments & Others
        lowerIngredient.contains("salt") -> "🧂"
        lowerIngredient.contains("honey") -> "🍯"
        lowerIngredient.contains("oil") || lowerIngredient.contains("olive") -> "🫒"
        lowerIngredient.contains("herb") || lowerIngredient.contains("basil") || lowerIngredient.contains("parsley") -> "🌿"
        lowerIngredient.contains("sugar") -> "🍬"
        lowerIngredient.contains("chocolate") || lowerIngredient.contains("cocoa") -> "🍫"
        lowerIngredient.contains("water") -> "💧"
        lowerIngredient.contains("wine") || lowerIngredient.contains("vinegar") -> "🍷"
        
        // Default
        else -> "✨"
    }
}

private fun getDaysUntilExpiration(item: FridgeItem): Int? {
    val expirationDate = item.expirationDate ?: return null
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    return (expirationDate.toEpochDays() - today.toEpochDays()).toInt()
}

@Composable
fun ExpiringSoonSection(
    items: List<FridgeItem>,
    onNavigateToFridge: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔥",
                    fontSize = 20.sp
                )
                Text(
                    text = "Use These Soon",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Items List
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                ExpiringItemCard(item = item)
            }
        }
    }
}

@Composable
private fun ExpiringItemCard(item: FridgeItem) {
    val daysUntil = getDaysUntilExpiration(item)
    val status = item.calculateFreshStatus()
    val urgencyColor = when (status) {
        FreshStatus.EXPIRED -> UrgentRed
        FreshStatus.URGENT -> WarmOrange
        else -> PrimaryGreen
    }

    val daysText = when {
        daysUntil == null -> "No date"
        daysUntil < 0 -> "Expired"
        daysUntil == 0 -> "Today"
        daysUntil == 1 -> "1 day"
        else -> "$daysUntil days"
    }

    val ingredientEmoji = getIngredientEmoji(item.name)

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Emoji
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                urgencyColor.copy(alpha = 0.15f),
                                PrimaryGreen.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ingredientEmoji,
                    fontSize = 24.sp
                )
            }

            // Name and Days
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = urgencyColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = daysText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = urgencyColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UrgentShoppingSection(
    items: List<ShoppingItem>,
    onNavigateToShopping: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🛒",
                    fontSize = 20.sp
                )
                Text(
                    text = "Urgent to Buy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (items.isNotEmpty()) {
                TextButton(
                    onClick = onNavigateToShopping,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen
                    )
                }
            }
        }
        
        // Items List
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                UrgentShoppingItemCard(item = item)
            }
        }
    }
}

@Composable
private fun UrgentShoppingItemCard(item: ShoppingItem) {
    val ingredientEmoji = getIngredientEmoji(item.name)
    
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                UrgentRed.copy(alpha = 0.15f),
                                PrimaryGreen.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ingredientEmoji,
                    fontSize = 28.sp
                )
            }
            
            // Name and Badge
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = UrgentRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Urgent",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = UrgentRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickMealsBanner(
    onDiscoverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF16664A), // Dark green background matching AICookingBanner
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background decorative element
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(120.dp)
                    .offset(x = 20.dp, y = 20.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    )
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column {
                    // Top section with feature tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Feature tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "QUICK & EASY",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Time tag
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E7F5C), // Slightly lighter green
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF16cb8a)) // Light green dot
                                )
                                Text(
                                    text = "15 min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Main content
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quick & Easy Meals",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 32.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Ready in under 15 minutes\nPerfect for busy days.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Discover Now Button
                    Button(
                        onClick = onDiscoverClick,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF16664A)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Discover Now",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16664A)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "→",
                                fontSize = 20.sp,
                                color = Color(0xFF16664A),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}
