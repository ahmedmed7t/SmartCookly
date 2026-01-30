package com.nexable.smartcookly.feature.onboarding.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexable.smartcookly.feature.onboarding.presentation.components.OnboardingProgressBar
import com.nexable.smartcookly.feature.onboarding.presentation.components.OnboardingTopBar
import com.nexable.smartcookly.feature.onboarding.presentation.steps.*
import com.nexable.smartcookly.platform.BackHandler
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_check
import smartcookly.composeapp.generated.resources.ic_next

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            // Top bar with back button and step indicator
            OnboardingTopBar(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps,
                onBackClick = { viewModel.goToPreviousStep() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Animated progress bar
            OnboardingProgressBar(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Step content with animation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300)
                            ) + fadeIn(tween(300)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { -it },
                                        animationSpec = tween(300)
                                    ) + fadeOut(tween(300))
                        } else {
                            slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(300)
                            ) + fadeIn(tween(300)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300)
                                    ) + fadeOut(tween(300))
                        }
                    },
                    label = "step_animation"
                ) { step ->
                    when (step) {
                        1 -> CuisineSelectionStep(
                            selectedCuisines = uiState.selectedCuisines,
                            otherCuisineText = uiState.otherCuisineText,
                            showOtherTextField = uiState.showOtherTextField,
                            onCuisineToggle = { viewModel.toggleCuisineSelection(it) },
                            onOtherTextChange = { viewModel.updateOtherCuisineText(it) }
                        )
                        2 -> DietaryStyleSelectionStep(
                            selectedDietaryStyle = uiState.selectedDietaryStyle,
                            otherDietaryStyleText = uiState.otherDietaryStyleText,
                            showOtherTextField = uiState.showOtherDietaryTextField,
                            onDietaryStyleSelect = { viewModel.selectDietaryStyle(it) },
                            onOtherTextChange = { viewModel.updateOtherDietaryStyleText(it) }
                        )
                        3 -> IngredientsAvoidanceStep(
                            avoidedIngredients = uiState.avoidedIngredients,
                            otherIngredientText = uiState.otherIngredientText,
                            showOtherTextField = uiState.showOtherIngredientTextField,
                            onIngredientToggle = { viewModel.toggleIngredientSelection(it) },
                            onOtherTextChange = { viewModel.updateOtherIngredientText(it) }
                        )
                        4 -> DislikedIngredientsStep(
                            dislikedIngredients = uiState.dislikedIngredients,
                            otherDislikedIngredientText = uiState.otherDislikedIngredientText,
                            showOtherTextField = uiState.showOtherDislikedIngredientTextField,
                            onIngredientToggle = { viewModel.toggleDislikedIngredientSelection(it) },
                            onOtherTextChange = { viewModel.updateOtherDislikedIngredientText(it) }
                        )
                        5 -> DiseaseSelectionStep(
                            selectedDiseases = uiState.selectedDiseases,
                            otherDiseaseText = uiState.otherDiseaseText,
                            showOtherTextField = uiState.showOtherDiseaseTextField,
                            onDiseaseToggle = { viewModel.toggleDiseaseSelection(it) },
                            onOtherTextChange = { viewModel.updateOtherDiseaseText(it) }
                        )
                        6 -> CookingLevelSelectionStep(
                            selectedCookingLevel = uiState.selectedCookingLevel,
                            onCookingLevelSelect = { viewModel.selectCookingLevel(it) }
                        )
                        else -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Unknown Step")
                        }
                    }
                }
            }

            // Bottom section with continue button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp, top = 12.dp)
                ) {
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
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (uiState.currentStep < uiState.totalSteps) "Continue" else "Get Started",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                painter = painterResource(
                                    if (uiState.currentStep < uiState.totalSteps) Res.drawable.ic_next 
                                    else Res.drawable.ic_check
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
