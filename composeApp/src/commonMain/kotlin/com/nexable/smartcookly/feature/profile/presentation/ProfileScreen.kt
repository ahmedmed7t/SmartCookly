package com.nexable.smartcookly.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.profile.presentation.components.ProfilePreferenceItem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_antibacterial
import smartcookly.composeapp.generated.resources.ic_back
import smartcookly.composeapp.generated.resources.ic_check
import smartcookly.composeapp.generated.resources.ic_close
import smartcookly.composeapp.generated.resources.ic_dietary
import smartcookly.composeapp.generated.resources.ic_edit
import smartcookly.composeapp.generated.resources.ic_health
import smartcookly.composeapp.generated.resources.ic_ingredients
import smartcookly.composeapp.generated.resources.ic_level
import smartcookly.composeapp.generated.resources.ic_menu

@Composable
fun ProfileScreen(
    onEditCuisines: () -> Unit,
    onEditDietary: () -> Unit,
    onEditAllergies: () -> Unit,
    onEditDisliked: () -> Unit,
    onEditHealth: () -> Unit,
    onEditCookingLevel: () -> Unit,
    onManageSubscription: () -> Unit = {},
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = koinInject(),
    refreshKey: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(uiState.displayName) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(refreshKey) {
        viewModel.loadProfile()
    }

    LaunchedEffect(uiState.displayName) {
        editedName = uiState.displayName
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Custom Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar with initials
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = uiState.displayName
                                .split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                .joinToString("")
                                .ifEmpty { "?" }
                            
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Name with edit
                        if (isEditingName) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = editedName,
                                    onValueChange = { editedName = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        if (editedName.isNotBlank()) {
                                            viewModel.updateDisplayName(editedName)
                                            isEditingName = false
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_check),
                                        contentDescription = "Save",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        editedName = uiState.displayName
                                        isEditingName = false
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_close),
                                        contentDescription = "Cancel",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = uiState.displayName.ifEmpty { "Your Name" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                IconButton(
                                    onClick = { isEditingName = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_edit),
                                        contentDescription = "Edit Name",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Tap to edit your name",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Cuisines & Diet Section
                SectionHeader(
                    title = "Cuisines & Diet",
                    emoji = "🍽️"
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfilePreferenceItem(
                    title = "Preferred Cuisines",
                    subtitle = uiState.cuisines.take(3).joinToString(", ")
                        .ifEmpty { "Not set" }
                        .let { if (uiState.cuisines.size > 3) "$it..." else it },
                    icon = painterResource(Res.drawable.ic_menu),
                    onClick = onEditCuisines
                )

                Spacer(modifier = Modifier.height(10.dp))

                ProfilePreferenceItem(
                    title = "Dietary Preferences",
                    subtitle = uiState.dietaryStyle ?: "Not set",
                    icon = painterResource(Res.drawable.ic_dietary),
                    onClick = onEditDietary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Health & Allergies Section
                SectionHeader(
                    title = "Health & Allergies",
                    emoji = "🏥"
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfilePreferenceItem(
                    title = "Allergies & Restrictions",
                    subtitle = uiState.avoidedIngredients.take(2).joinToString(", ")
                        .ifEmpty { "Not set" }
                        .let { if (uiState.avoidedIngredients.size > 2) "$it..." else it },
                    icon = painterResource(Res.drawable.ic_antibacterial),
                    onClick = onEditAllergies
                )

                Spacer(modifier = Modifier.height(10.dp))

                ProfilePreferenceItem(
                    title = "Disliked Ingredients",
                    subtitle = uiState.dislikedIngredients.take(2).joinToString(", ")
                        .ifEmpty { "Not set" }
                        .let { if (uiState.dislikedIngredients.size > 2) "$it..." else it },
                    icon = painterResource(Res.drawable.ic_ingredients),
                    onClick = onEditDisliked
                )

                Spacer(modifier = Modifier.height(10.dp))

                ProfilePreferenceItem(
                    title = "Health Conditions",
                    subtitle = uiState.diseases.take(2).joinToString(", ")
                        .ifEmpty { "Not set" }
                        .let { if (uiState.diseases.size > 2) "$it..." else it },
                    icon = painterResource(Res.drawable.ic_health),
                    onClick = onEditHealth
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Cooking Section
                SectionHeader(
                    title = "Cooking",
                    emoji = "👨‍🍳"
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfilePreferenceItem(
                    title = "Cooking Level",
                    subtitle = uiState.cookingLevel ?: "Not set",
                    icon = painterResource(Res.drawable.ic_level),
                    onClick = onEditCookingLevel
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Manage Subscription button
                OutlinedButton(
                    onClick = onManageSubscription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💳",
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Manage Subscription",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout button
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🚪",
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "👋", fontSize = 24.sp)
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to logout? You'll need to sign in again to access your data.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Logout",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
