package com.nexable.smartcookly.feature.profile.presentation.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.presentation.steps.CuisineSelectionStep
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCuisinesScreen(
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: EditPreferenceViewModel = koinInject(),
    authRepository: AuthRepository = koinInject()
) {
    val selectedCuisines by viewModel.selectedCuisines.collectAsState()
    val otherCuisineText by viewModel.otherCuisineText.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val showOtherTextField = selectedCuisines.contains(Cuisine.OTHER)
    
    LaunchedEffect(Unit) {
        viewModel.loadCurrentPreferences()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Cuisines") },
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
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    val scope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            val userId = authRepository.getCurrentUser()?.uid
                            if (userId != null) {
                                scope.launch {
                                    viewModel.savePreferences(userId).fold(
                                        onSuccess = { onSaveComplete() },
                                        onFailure = { /* Handle error */ }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CuisineSelectionStep(
                selectedCuisines = selectedCuisines,
                otherCuisineText = otherCuisineText,
                showOtherTextField = showOtherTextField,
                onCuisineToggle = { viewModel.toggleCuisine(it) },
                onOtherTextChange = { viewModel.updateOtherCuisineText(it) }
            )
        }
    }
}
