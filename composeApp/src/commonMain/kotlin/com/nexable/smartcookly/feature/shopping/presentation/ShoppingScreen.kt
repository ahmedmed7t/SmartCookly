package com.nexable.smartcookly.feature.shopping.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.shopping.data.model.ShoppingItem
import com.nexable.smartcookly.feature.shopping.data.model.Urgency
import kotlinx.datetime.Clock
import org.koin.compose.koinInject

private val PrimaryGreen = Color(0xFF16664A)
private val WarmOrange = Color(0xFFFF9500)
private val UrgentRed = Color(0xFFE74C3C)

// Reuse ingredient emoji function
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

private fun formatTimeAgo(timestamp: Long): String {
    if (timestamp == 0L) return "Just now"
    val now = Clock.System.now().epochSeconds
    val diff = now - timestamp
    
    return when {
        diff < 60 -> "Just now"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86400 -> "${diff / 3600}h ago"
        diff < 604800 -> "${diff / 86400}d ago"
        else -> "${diff / 604800}w ago"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    // Group items by urgency
    val groupedItems = remember(uiState.items) {
        uiState.items.groupBy { it.urgency }
    }
    
    val urgentItems = groupedItems[Urgency.HIGH] ?: emptyList()
    val normalItems = groupedItems[Urgency.NORMAL] ?: emptyList()
    val lowItems = groupedItems[Urgency.LOW] ?: emptyList()
    
    val tabs = listOf(
        Triple("Urgent", "🔥", urgentItems.size),
        Triple("Need Soon", "📋", normalItems.size),
        Triple("Can Wait", "⏰", lowItems.size)
    )
    
    val currentItems = when (selectedTabIndex) {
        0 -> urgentItems
        1 -> normalItems
        2 -> lowItems
        else -> emptyList()
    }
    
    Scaffold(
        topBar = {
            Column(modifier = Modifier) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Shopping List",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (!uiState.isEmpty && !uiState.isLoading) {
                                Text(
                                    text = "${uiState.items.size} ${if (uiState.items.size == 1) "item" else "items"}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    },
                    actions = {
                        if (!uiState.isEmpty && !uiState.isLoading) {
                            IconButton(
                                onClick = { showDeleteAllDialog = true },
                                enabled = !uiState.isDeletingAll
                            ) {
                                if (uiState.isDeletingAll) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = PrimaryGreen
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete All",
                                        tint = UrgentRed
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                
                // Horizontal Tabs
                if (!uiState.isEmpty && !uiState.isLoading) {
                    ShoppingTabRow(
                        tabs = tabs,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        when {
            uiState.isLoading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = paddingValues.calculateStartPadding(layoutDirection),
                            end = paddingValues.calculateEndPadding(layoutDirection),
                            top = paddingValues.calculateTopPadding()
                        )
                )
            }
            uiState.error != null -> {
                ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadItems() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = paddingValues.calculateStartPadding(layoutDirection),
                            end = paddingValues.calculateEndPadding(layoutDirection),
                            top = paddingValues.calculateTopPadding()
                        )
                )
            }
            uiState.isEmpty -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = paddingValues.calculateStartPadding(layoutDirection),
                            end = paddingValues.calculateEndPadding(layoutDirection),
                            top = paddingValues.calculateTopPadding()
                        )
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = paddingValues.calculateStartPadding(layoutDirection),
                            end = paddingValues.calculateEndPadding(layoutDirection),
                            top = paddingValues.calculateTopPadding()
                        )
                ) {
                    if (currentItems.isEmpty()) {
                        EmptyTabState(
                            urgency = when (selectedTabIndex) {
                                0 -> Urgency.HIGH
                                1 -> Urgency.NORMAL
                                else -> Urgency.LOW
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ShoppingList(
                            items = currentItems,
                            isDeleting = uiState.isDeleting,
                            onMarkAsBought = { itemId -> viewModel.deleteItem(itemId) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        
        // Delete All Confirmation Dialog
        if (showDeleteAllDialog) {
            DeleteAllConfirmationDialog(
                onConfirm = {
                    showDeleteAllDialog = false
                    viewModel.deleteAllItems()
                },
                onDismiss = { showDeleteAllDialog = false }
            )
        }
    }
}

@Composable
private fun ShoppingTabRow(
    tabs: List<Triple<String, String, Int>>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .clip(RoundedCornerShape(8.dp))
                    .height(3.dp),
                color = PrimaryGreen
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, (label, emoji, count) ->
            val isSelected = selectedTabIndex == index
            val tabColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(300),
                label = "tabColor"
            )
            
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = emoji,
                        fontSize = 16.sp
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = tabColor
                    )
                    if (count > 0) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) PrimaryGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = PrimaryGreen)
            Text(
                text = "Loading shopping list...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚠️",
                fontSize = 64.sp
            )
            Text(
                text = "Failed to load shopping list",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🛒",
                fontSize = 64.sp
            )
            Text(
                text = "Your Shopping List is Empty",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Add ingredients from recipes to your shopping list!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyTabState(
    urgency: Urgency,
    modifier: Modifier = Modifier
) {
    val (emoji, message, color) = when (urgency) {
        Urgency.HIGH -> Triple("🔥", "No urgent items", UrgentRed)
        Urgency.NORMAL -> Triple("📋", "No items needed soon", WarmOrange)
        Urgency.LOW -> Triple("⏰", "No items to wait for", PrimaryGreen)
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 64.sp
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Great job keeping your list organized!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ShoppingList(
    items: List<ShoppingItem>,
    isDeleting: String?,
    onMarkAsBought: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            ShoppingItemCard(
                item = item,
                isDeleting = isDeleting == item.id,
                onMarkAsBought = { onMarkAsBought(item.id) }
            )
        }
    }
}

@Composable
private fun ShoppingItemCard(
    item: ShoppingItem,
    isDeleting: Boolean,
    onMarkAsBought: () -> Unit
) {
    val urgencyColor = when (item.urgency) {
        Urgency.HIGH -> UrgentRed
        Urgency.NORMAL -> WarmOrange
        Urgency.LOW -> PrimaryGreen
    }
    
    val ingredientEmoji = getIngredientEmoji(item.name)
    val timeAgo = formatTimeAgo(item.addedAt)
    
    var isMarkedAsBought by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isMarkedAsBought) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left border accent
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                urgencyColor,
                                urgencyColor.copy(alpha = 0.6f)
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Ingredient emoji
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                urgencyColor.copy(alpha = 0.15f),
                                PrimaryGreen.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ingredientEmoji,
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Item details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (isMarkedAsBought) TextDecoration.LineThrough else null
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = urgencyColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = when (item.urgency) {
                                Urgency.HIGH -> "Urgent"
                                Urgency.NORMAL -> "Need Soon"
                                Urgency.LOW -> "Can Wait"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = urgencyColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Mark as bought button
            IconButton(
                onClick = {
                    isMarkedAsBought = true
                    onMarkAsBought()
                },
                enabled = !isDeleting && !isMarkedAsBought,
                modifier = Modifier.size(48.dp)
            ) {
                if (isDeleting || isMarkedAsBought) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = PrimaryGreen
                    )
                } else {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryGreen.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Mark as Bought",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteAllConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        UrgentRed.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = UrgentRed,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Delete All Items?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete all items from your shopping list?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UrgentRed
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delete All",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    )
}
