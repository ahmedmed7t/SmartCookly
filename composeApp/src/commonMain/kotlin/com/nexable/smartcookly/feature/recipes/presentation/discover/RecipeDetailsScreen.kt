package com.nexable.smartcookly.feature.recipes.presentation.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.shopping.presentation.AddToShoppingDialog
import com.nexable.smartcookly.feature.shopping.presentation.ShoppingViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_heart
import smartcookly.composeapp.generated.resources.ic_youtube
import com.nexable.smartcookly.platform.rememberOpenUrl

private val PrimaryGreen = Color(0xFF16664A)

// Helper function to create YouTube search URL from recipe name
private fun createYouTubeSearchUrl(recipeName: String): String {
    val query = "how to cook $recipeName".replace(" ", "+")
    return "https://www.youtube.com/results?search_query=$query"
}

@Composable
fun RecipeDetailsScreen(
    recipe: Recipe?,
    onStartCooking: () -> Unit = {},
    isAddingFavorite: Boolean = false,
    isFavorited: Boolean = false,
    onAddToFavorites: () -> Unit = {},
    showFavoriteButton: Boolean = true,
    modifier: Modifier = Modifier,
    shoppingViewModel: ShoppingViewModel = koinInject()
) {
    var showShoppingDialog by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf("") }
    val shoppingUiState by shoppingViewModel.uiState.collectAsState()
    val openUrl = rememberOpenUrl()
    
    // Close dialog when item is successfully added
    LaunchedEffect(shoppingUiState.isAdding) {
        if (!shoppingUiState.isAdding && shoppingUiState.error == null && showShoppingDialog) {
            // Item was successfully added, close dialog
            showShoppingDialog = false
        }
    }
    
    // Clear error when dialog is dismissed
    LaunchedEffect(showShoppingDialog) {
        if (!showShoppingDialog && shoppingUiState.error != null) {
            // Error will be cleared on next add attempt
        }
    }
    
    if (recipe == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Recipe not found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Recipe Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            if (recipe.imageUrl.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = PrimaryGreen,
                                strokeWidth = 4.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🍽️",
                                fontSize = 64.sp
                            )
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍽️",
                        fontSize = 64.sp
                    )
                }
            }
            
            // Favorite button overlay - only show if showFavoriteButton is true
            if (showFavoriteButton) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(32.dp)
                ) {
                    IconButton(
                        onClick = onAddToFavorites,
                        enabled = !isAddingFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (isAddingFavorite) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryGreen
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.ic_heart),
                                contentDescription = "Favorite",
                                tint = if (isFavorited) Color(0xFFE74C3C) else Color(0xFF95A5A6), // Red when favorited, grey otherwise
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Recipe Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cuisine Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimaryGreen.copy(alpha = 0.15f)
            ) {
                Text(
                    text = recipe.cuisine.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            
            // Recipe Name
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Description
            if (recipe.description.isNotEmpty()) {
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Info Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    icon = "⏱️",
                    label = "Time",
                    value = "${recipe.cookingTimeMinutes} min",
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    icon = "⭐",
                    label = "Rating",
                    value = "${recipe.rating}",
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    icon = "📊",
                    label = "Fit",
                    value = "${recipe.fitPercentage}%",
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Start Cooking Button and YouTube Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Cooking Button (takes most width)
                Button(
                    onClick = onStartCooking,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "👨‍🍳",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Ready to Cook?",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "Let's Start Cooking!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                
                // YouTube Button (always show - search URL always works)
                IconButton(
                    onClick = { openUrl(createYouTubeSearchUrl(recipe.name)) },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFE62117), // YouTube red
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_youtube),
                        contentDescription = "Watch on YouTube",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Ingredients Section
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                recipe.ingredients.forEach { ingredient ->
                    IngredientItem(
                        ingredient = ingredient,
                        onAddToCart = {
                            selectedIngredient = ingredient
                            showShoppingDialog = true
                        }
                    )
                }
            }
        }
        
        // Add to Shopping Dialog
        if (showShoppingDialog) {
            AddToShoppingDialog(
                initialIngredientName = selectedIngredient,
                onAdd = { name, urgency ->
                    shoppingViewModel.addItem(name, urgency)
                },
                onDismiss = { 
                    showShoppingDialog = false
                    shoppingViewModel.clearError()
                },
                isAdding = shoppingUiState.isAdding,
                error = shoppingUiState.error
            )
        }
    }
}

@Composable
private fun InfoCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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

@Composable
private fun IngredientItem(
    ingredient: String,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ingredientEmoji = getIngredientEmoji(ingredient)
    val AccentBlue = Color(0xFF3498DB)
    val WarmOrange = Color(0xFFFF9500)
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Ingredient emoji with gradient background
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    PrimaryGreen.copy(alpha = 0.15f),
                                    WarmOrange.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ingredientEmoji,
                        fontSize = 22.sp
                    )
                }
                
                // Ingredient name
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = ingredient,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tap cart to add to list",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Add to cart button with gradient
            Surface(
                onClick = onAddToCart,
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    AccentBlue.copy(alpha = 0.15f),
                                    AccentBlue.copy(alpha = 0.25f)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛒",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

