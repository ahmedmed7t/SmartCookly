package com.nexable.smartcookly.feature.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.onboarding.presentation.components.OnboardingProgressBar
import com.nexable.smartcookly.feature.onboarding.presentation.components.OnboardingTopBar
import com.nexable.smartcookly.feature.onboarding.presentation.steps.CuisineSelectionStep
import com.nexable.smartcookly.feature.onboarding.presentation.steps.DietaryStyleSelectionStep
import com.nexable.smartcookly.feature.onboarding.presentation.steps.IngredientsAvoidanceStep
import com.nexable.smartcookly.feature.onboarding.presentation.steps.DislikedIngredientsStep
import com.nexable.smartcookly.feature.onboarding.presentation.steps.DiseaseSelectionStep
import com.nexable.smartcookly.platform.BackHandler
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle system back button
    BackHandler(enabled = uiState.currentStep > 1) {
        viewModel.goToPreviousStep()
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top bar with back, skip, and step indicator
            OnboardingTopBar(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps,
                onBackClick = { viewModel.goToPreviousStep() }
            )
            
            // Progress bar
            OnboardingProgressBar(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp)
            )

            // Content area with white surface
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    // Step content - switch based on current step
                    when (uiState.currentStep) {
                        1 -> {
                            // Step 1: Favorite Cuisines
                            CuisineSelectionStep(
                                selectedCuisines = uiState.selectedCuisines,
                                otherCuisineText = uiState.otherCuisineText,
                                showOtherTextField = uiState.showOtherTextField,
                                onCuisineToggle = { cuisine ->
                                    viewModel.toggleCuisineSelection(cuisine)
                                },
                                onOtherTextChange = { text ->
                                    viewModel.updateOtherCuisineText(text)
                                }
                            )
                        }
                        2 -> {
                            // Step 2: Dietary Style & Restrictions
                            DietaryStyleSelectionStep(
                                selectedDietaryStyle = uiState.selectedDietaryStyle,
                                otherDietaryStyleText = uiState.otherDietaryStyleText,
                                showOtherTextField = uiState.showOtherDietaryTextField,
                                onDietaryStyleSelect = { style ->
                                    viewModel.selectDietaryStyle(style)
                                },
                                onOtherTextChange = { text ->
                                    viewModel.updateOtherDietaryStyleText(text)
                                }
                            )
                        }
                        3 -> {
                            // Step 3: Ingredients to Avoid
                            IngredientsAvoidanceStep(
                                avoidedIngredients = uiState.avoidedIngredients,
                                otherIngredientText = uiState.otherIngredientText,
                                showOtherTextField = uiState.showOtherIngredientTextField,
                                onIngredientToggle = { ingredient ->
                                    viewModel.toggleIngredientSelection(ingredient)
                                },
                                onOtherTextChange = { text ->
                                    viewModel.updateOtherIngredientText(text)
                                }
                            )

                        }
                        4 -> {
                            // Step 4: Disliked Ingredients
                            DislikedIngredientsStep(
                                dislikedIngredients = uiState.dislikedIngredients,
                                otherDislikedIngredientText = uiState.otherDislikedIngredientText,
                                showOtherTextField = uiState.showOtherDislikedIngredientTextField,
                                onIngredientToggle = { ingredient ->
                                    viewModel.toggleDislikedIngredientSelection(ingredient)
                                },
                                onOtherTextChange = { text ->
                                    viewModel.updateOtherDislikedIngredientText(text)
                                }
                            )
                        }
                        5 -> {
                            // Step 5: Health Conditions / Diseases
                            DiseaseSelectionStep(
                                selectedDiseases = uiState.selectedDiseases,
                                otherDiseaseText = uiState.otherDiseaseText,
                                showOtherTextField = uiState.showOtherDiseaseTextField,
                                onDiseaseToggle = { disease ->
                                    viewModel.toggleDiseaseSelection(disease)
                                },
                                onOtherTextChange = { text ->
                                    viewModel.updateOtherDiseaseText(text)
                                }
                            )
                        }
                        else -> {
                            // Fallback
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text("Unknown Step")
                            }
                        }
                    }
                }
            }
            
            // Continue button at bottom
            Button(
                onClick = {
                    if (uiState.currentStep < uiState.totalSteps) {
                        viewModel.goToNextStep()
                    } else {
                        viewModel.completeOnboarding(onOnboardingComplete)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (uiState.currentStep < uiState.totalSteps) "Continue" else "Get Started",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
