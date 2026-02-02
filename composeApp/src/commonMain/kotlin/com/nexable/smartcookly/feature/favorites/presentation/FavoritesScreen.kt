package com.nexable.smartcookly.feature.favorites.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.presentation.discover.components.RecipeCard
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

private val PrimaryGreen = Color(0xFF16664A)
private val FavoriteRed = Color(0xFFE74C3C)
private val FavoriteRedLight = Color(0xFFFFEBEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    onViewRecipe: (Recipe) -> Unit = {},
    viewModel: FavoritesViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Favorites",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadFavorites() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.isEmpty -> {
                EmptyState(modifier = Modifier.padding(paddingValues))
            }
            else -> {
                FavoritesList(
                    favorites = uiState.favorites,
                    isRemoving = uiState.isRemoving,
                    onRemove = { recipeId -> viewModel.removeFromFavorites(recipeId) },
                    onViewRecipe = onViewRecipe,
                    modifier = Modifier.padding(paddingValues)
                )
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
                text = "Loading favorites...",
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
                text = "Failed to load favorites",
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
                text = "❤️",
                fontSize = 64.sp
            )
            Text(
                text = "No Favorites Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Start cooking and add recipes to your favorites to see them here!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FavoritesList(
    favorites: List<Recipe>,
    isRemoving: String?,
    onRemove: (String) -> Unit,
    onViewRecipe: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = favorites,
            key = { it.id }
        ) { recipe ->
            FavoriteRecipeItem(
                recipe = recipe,
                isRemoving = isRemoving == recipe.id,
                onRemove = { onRemove(recipe.id) },
                onViewRecipe = { onViewRecipe(recipe) }
            )
        }
    }
}

@Composable
private fun FavoriteRecipeItem(
    recipe: Recipe,
    isRemoving: Boolean,
    onRemove: () -> Unit,
    onViewRecipe: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    // Animation for heart scale when pressed
    val heartScale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heartScale"
    )
    
    // Confirmation Dialog
    if (showConfirmDialog) {
        RemoveConfirmationDialog(
            recipeName = recipe.name,
            onConfirm = {
                showConfirmDialog = false
                onRemove()
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        RecipeCard(
            recipe = recipe,
            onViewRecipeClick = onViewRecipe,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Favorite heart button overlay - top right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(44.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = FavoriteRed.copy(alpha = 0.3f),
                    spotColor = FavoriteRed.copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !isRemoving
                ) {
                    isPressed = true
                    showConfirmDialog = true
                }
                .scale(heartScale),
            contentAlignment = Alignment.Center
        ) {
            if (isRemoving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = FavoriteRed
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = FavoriteRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    
    // Reset pressed state
    LaunchedEffect(showConfirmDialog) {
        if (!showConfirmDialog) {
            isPressed = false
        }
    }
}

@Composable
private fun RemoveConfirmationDialog(
    recipeName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(FavoriteRedLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = FavoriteRed,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Remove from Favorites?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Are you sure you want to remove \"$recipeName\" from your favorites?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FavoriteRed
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Remove",
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
                    contentColor = Color(0xFF6B7280)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(Color(0xFFE5E7EB), Color(0xFFE5E7EB)))
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
