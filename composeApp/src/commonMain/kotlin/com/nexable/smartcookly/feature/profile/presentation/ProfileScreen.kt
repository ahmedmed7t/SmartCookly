package com.nexable.smartcookly.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.profile.presentation.components.ProfilePreferenceItem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_antibacterial
import smartcookly.composeapp.generated.resources.ic_back
import smartcookly.composeapp.generated.resources.ic_dietary
import smartcookly.composeapp.generated.resources.ic_health
import smartcookly.composeapp.generated.resources.ic_ingredients
import smartcookly.composeapp.generated.resources.ic_level
import smartcookly.composeapp.generated.resources.ic_menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditCuisines: () -> Unit,
    onEditDietary: () -> Unit,
    onEditAllergies: () -> Unit,
    onEditDisliked: () -> Unit,
    onEditHealth: () -> Unit,
    onEditCookingLevel: () -> Unit,
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
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Name field
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Name",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditingName) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (editedName.isNotBlank()) {
                                        viewModel.updateDisplayName(editedName)
                                        isEditingName = false
                                    }
                                }
                            ) {
//                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                            IconButton(
                                onClick = {
                                    editedName = uiState.displayName
                                    isEditingName = false
                                }
                            ) {
//                                Icon(Icons.Default.Close, contentDescription = "Cancel")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.displayName.ifEmpty { "Your Name" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { isEditingName = true }) {
//                                Icon(Icons.Default.Edit, contentDescription = "Edit Name")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Cuisines & Diet Section
            Text(
                text = "Cuisines & Diet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            ProfilePreferenceItem(
                title = "Preferred Cuisines",
                subtitle = uiState.cuisines.take(3).joinToString(", ")
                    .ifEmpty { "Not set" }
                    .let { if (uiState.cuisines.size > 3) "$it..." else it },
                icon = painterResource(Res.drawable.ic_menu),
                onClick = onEditCuisines,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ProfilePreferenceItem(
                title = "Dietary Preferences",
                subtitle = uiState.dietaryStyle ?: "Not set",
                icon = painterResource(Res.drawable.ic_dietary),
                onClick = onEditDietary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Health & Allergies Section
            Text(
                text = "Health & Allergies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            ProfilePreferenceItem(
                title = "Allergies & Restrictions",
                subtitle = uiState.avoidedIngredients.take(2).joinToString(", ")
                    .ifEmpty { "Not set" }
                    .let { if (uiState.avoidedIngredients.size > 2) "$it..." else it },
                icon = painterResource(Res.drawable.ic_antibacterial),
                onClick = onEditAllergies,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ProfilePreferenceItem(
                title = "Disliked Ingredients",
                subtitle = uiState.dislikedIngredients.take(2).joinToString(", ")
                    .ifEmpty { "Not set" }
                    .let { if (uiState.dislikedIngredients.size > 2) "$it..." else it },
                icon = painterResource(Res.drawable.ic_ingredients),
                onClick = onEditDisliked,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ProfilePreferenceItem(
                title = "Health Conditions",
                subtitle = uiState.diseases.take(2).joinToString(", ")
                    .ifEmpty { "Not set" }
                    .let { if (uiState.diseases.size > 2) "$it..." else it },
                icon = painterResource(Res.drawable.ic_health),
                onClick = onEditHealth,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cooking Section
            Text(
                text = "Cooking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            ProfilePreferenceItem(
                title = "Cooking Level",
                subtitle = uiState.cookingLevel ?: "Not set",
                icon = painterResource(Res.drawable.ic_level),
                onClick = onEditCookingLevel,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logout button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Logout")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text("Logout", color =  Color(0xFF16664A))
                },
                text = {
                    Text("Are you sure you want to logout?", color =  Color(0xFF16664A))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        }
                    ) {
                        Text("Logout", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancel", color =  Color(0xFF16664A))
                    }
                }
            )
        }
    }
}
