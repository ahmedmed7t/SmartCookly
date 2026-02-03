package com.nexable.smartcookly.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_chef
import smartcookly.composeapp.generated.resources.ic_cooking
import smartcookly.composeapp.generated.resources.ic_heart

@Composable
fun HomeScreen(
    onScanFridgeClick: () -> Unit = {},
    onStartCookingClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onNavigateToFridge: () -> Unit = {},
    onNavigateToShopping: () -> Unit = {},
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = koinInject(),
    viewModel: HomeViewModel = koinInject()
) {
    val greeting = getGreeting()
    val currentUser = authRepository.getCurrentUser()
    val displayName = currentUser?.displayName?.takeIf { it.isNotBlank() }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding()
                )
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            HomeHeader(
                greeting = greeting,
                displayName = displayName,
                onProfileClick = onProfileClick,
                onFavoritesClick = onFavoritesClick
            )

            // AI Cooking Banner
            AICookingBanner(
                onStartCookingClick = onStartCookingClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Expiring Soon Section - only show if there are items
            if (uiState.expiringItems.isNotEmpty()) {
                ExpiringSoonSection(
                    items = uiState.expiringItems,
                    onNavigateToFridge = onNavigateToFridge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Urgent Shopping Section - only show if there are items
            if (uiState.urgentShoppingItems.isNotEmpty()) {
                UrgentShoppingSection(
                    items = uiState.urgentShoppingItems,
                    onNavigateToShopping = onNavigateToShopping,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Cooking Tips Carousel
            CookingTipsCarousel(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    displayName: String?,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val firstName = displayName?.split(" ")?.firstOrNull() ?: "Chef"

    val greetingIcon = when {
        greeting.contains("MORNING", ignoreCase = true) -> "☀️"
        greeting.contains("AFTERNOON", ignoreCase = true) -> "🌤️"
        greeting.contains("EVENING", ignoreCase = true) -> "🌅"
        else -> "🌙"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Avatar with Chef Icon (Left)
        Column(
            modifier = Modifier.clickable(onClick = onProfileClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Circle with border
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {

                    // Smaller chef hat icon
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_chef),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.size(38.dp).align(Alignment.TopCenter).padding(top = 4.dp)
                        )


                        // First character of user name
                        Text(
                            text = firstName.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                        )
                    }
                }

                // Sparkle decoration
                Text(
                    text = "✨",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // "Profile" text below the circle
            Text(
                text = "Profile",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Greeting and Name Section (Middle)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Greeting on top with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = greetingIcon,
                    fontSize = 16.sp
                )
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Name below
            Text(
                text = firstName.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Favourites Button (Right) - Outlined circle with thinner border
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onFavoritesClick),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_heart),
                    contentDescription = "Favourites",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
private fun AICookingBanner(
    onStartCookingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF16664A), // Dark green background
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background decorative element (fork and knife silhouette)
            // Using a placeholder - you can replace with actual icon
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
                    // Top section with feature tag and ingredient count
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Feature tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Placeholder for sparkle/magic icon
                            Text(
                                text = "✨",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "FRIDGE AI VISION",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Ingredient count tag
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
                                    text = "Ingredients Detected",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // Main content - positioned in the middle
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Find Recipes\nfrom My Fridge",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Let AI scan your ingredients\nand suggest a perfect meal\nin seconds.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Start Cooking Button - positioned at bottom
                    Button(
                        onClick = onStartCookingClick,
                        modifier = Modifier
                            .height(48.dp),
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
                                text = "Start Cooking",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16664A)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            // Placeholder for scan/camera icon - use icon name: ic_scan_fridge
                            Icon(
                                painter = painterResource(Res.drawable.ic_cooking), // Placeholder - replace with ic_scan_fridge
                                contentDescription = "Start Cooking",
                                tint = Color(0xFF16664A),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun getGreeting(): String {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = currentTime.hour
    return when (hour) {
        in 5..11 -> "GOOD MORNING"
        in 12..17 -> "GOOD AFTERNOON"
        in 18..21 -> "GOOD EVENING"
        else -> "GOOD NIGHT"
    }
}
