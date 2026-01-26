package com.nexable.smartcookly.feature.profile.presentation.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient
import com.nexable.smartcookly.feature.onboarding.presentation.steps.IngredientsAvoidanceStep
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAllergiesScreen(
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: EditPreferenceViewModel = koinInject(),
    authRepository: AuthRepository = koinInject()
) {
    val avoidedIngredients by viewModel.avoidedIngredients.collectAsState()
    val otherIngredientText by viewModel.otherIngredientText.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val showOtherTextField = avoidedIngredients.contains(Ingredient.OTHER)

    LaunchedEffect(Unit) {
        viewModel.loadCurrentPreferences()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Allergies & Restrictions",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp)
                    )
                },
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
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
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
            IngredientsAvoidanceStep(
                avoidedIngredients = avoidedIngredients,
                otherIngredientText = otherIngredientText,
                showOtherTextField = showOtherTextField,
                onIngredientToggle = { viewModel.toggleAvoidedIngredient(it) },
                onOtherTextChange = { viewModel.updateOtherIngredientText(it) }
            )
        }
    }
}
