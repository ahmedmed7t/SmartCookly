package com.nexable.smartcookly.feature.home.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import smartcookly.composeapp.generated.resources.ic_fridge
import smartcookly.composeapp.generated.resources.ic_heart
import smartcookly.composeapp.generated.resources.ic_user

@Composable
fun HomeScreen(
    onScanFridgeClick: () -> Unit = {},
    onStartCookingClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = koinInject()
) {
    val greeting = getGreeting()
    val currentUser = authRepository.getCurrentUser()
    val displayName = currentUser?.displayName?.takeIf { it.isNotBlank() }
    val chefName = if (displayName != null) {
        "Chef, $displayName"
    } else {
        "Smart Chef"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Section
            HomeHeader(
                greeting = greeting,
                chefName = chefName,
                onProfileClick = onProfileClick,
            )

            // AI Cooking Banner
            AICookingBanner(
                onStartCookingClick = onStartCookingClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Rest of the home content can be added here
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    chefName: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF16664A),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = chefName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Fav Icon
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF16664A).copy(alpha = 0.1f) // Light brown background
                    )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_heart),
                    contentDescription = "Favourite",
                    tint = Color(0xFF16664A), // Brown color
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            // Profile Icon
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF16664A).copy(alpha = 0.1f) // Light brown background
                    )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_chef),
                    contentDescription = "Profile",
                    tint = Color(0xFF16664A),
                    modifier = Modifier.size(40.dp)
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
