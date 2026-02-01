package com.nexable.smartcookly.feature.recipes.presentation.cooking

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.rotate
import com.nexable.smartcookly.platform.BackHandler
import org.koin.compose.koinInject
import kotlinx.coroutines.delay

private val PrimaryGreen = Color(0xFF16664A)
private val TimerAmber = Color(0xFFF39C12)
private val TimerRed = Color(0xFFE74C3C)

private val cookingEmojis = listOf("👨‍🍳", "🍳", "🥘", "🍲", "🥗", "🍝", "🍜", "🥡")
private val loadingTips = listOf(
    "Preparing your cooking guide...",
    "Gathering step-by-step instructions...",
    "Almost ready to start cooking..."
)

@Composable
fun CookingModeScreen(
    recipeName: String,
    ingredients: List<String>,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onCompletionScreenVisibilityChanged: (Boolean) -> Unit = {},
    viewModel: CookingModeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load cooking steps when recipe changes (cached if already loaded)
    LaunchedEffect(recipeName, ingredients) {
        viewModel.loadCookingSteps(recipeName, ingredients)
    }
    
    // Notify parent about completion screen visibility
    LaunchedEffect(uiState.isCookingComplete) {
        onCompletionScreenVisibilityChanged(uiState.isCookingComplete)
    }
    
    // Handle system back button - navigate to home if cooking is complete
    BackHandler(enabled = uiState.isCookingComplete) {
        onNavigateToHome()
    }
    
    // Show completion screen if cooking is finished
    if (uiState.isCookingComplete) {
        CookingCompleteScreen(
            recipeName = recipeName,
            onNavigateToHome = onNavigateToHome,
            onAddToFavorites = {
                // TODO: Implement add to favorites functionality
            }
        )
        return
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                CookingLoadingState()
            }
            uiState.error != null -> {
                ErrorState(
                    error = uiState.error!!,
                    onRetry = {
                        viewModel.loadCookingSteps(recipeName, ingredients)
                    }
                )
            }
            uiState.steps.isEmpty() -> {
                EmptyState(onNavigateBack = onNavigateBack)
            }
            else -> {
                CookingContent(
                    uiState = uiState,
                    onNextStep = {
                        if (uiState.isLastStep) {
                            viewModel.finishCooking()
                        } else {
                            viewModel.nextStep()
                        }
                    },
                    onPreviousStep = { viewModel.previousStep() },
                    onStartTimer = { minutes -> viewModel.startTimer(minutes) },
                    onPauseTimer = { viewModel.pauseTimer() },
                    onResumeTimer = { viewModel.resumeTimer() },
                    onResetTimer = { viewModel.resetTimer() }
                )
            }
        }
    }
}

@Composable
private fun CookingLoadingState() {
    var currentTipIndex by remember { mutableStateOf(0) }
    var currentEmojiIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            currentTipIndex = (currentTipIndex + 1) % loadingTips.size
        }
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentEmojiIndex = (currentEmojiIndex + 1) % cookingEmojis.size
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = cookingEmojis[currentEmojiIndex],
            fontSize = 64.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = loadingTips[currentTipIndex],
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryGreen
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            fontSize = 64.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Failed to load cooking steps",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
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

@Composable
private fun EmptyState(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🍽️",
            fontSize = 64.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No cooking steps available",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Recipe")
        }
    }
}

@Composable
private fun CookingContent(
    uiState: CookingModeUiState,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onStartTimer: (Int) -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    val currentStep = uiState.currentStep ?: return
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Step Progress Header
            StepProgressHeader(
                currentStep = uiState.currentStepIndex + 1,
                totalSteps = uiState.steps.size,
                progress = uiState.progress
            )
            
            // Step Content Card
            StepContentCard(description = currentStep.description)
            
            // Ingredients Section
            if (currentStep.ingredientsUsed.isNotEmpty()) {
                IngredientsSection(ingredients = currentStep.ingredientsUsed)
            }
            
            // Timer Card
            if (currentStep.timeMinutes > 0) {
                TimerCard(
                    timeMinutes = currentStep.timeMinutes,
                    timerSeconds = uiState.timerSeconds,
                    timerRunning = uiState.timerRunning,
                    timerFinished = uiState.timerFinished,
                    onStartTimer = { onStartTimer(currentStep.timeMinutes) },
                    onPauseTimer = onPauseTimer,
                    onResumeTimer = onResumeTimer,
                    onResetTimer = onResetTimer
                )
            }
        }

        // Navigation Buttons (Fixed at bottom)
        NavigationButtonsRow(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentStep = uiState.currentStepIndex + 1,
            totalSteps = uiState.steps.size,
            isFirstStep = uiState.isFirstStep,
            isLastStep = uiState.isLastStep,
            onPrevious = onPreviousStep,
            onNext = onNextStep
        )
    }
}

@Composable
private fun StepProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    progress: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Step Number Badge
                Surface(
                    shape = CircleShape,
                    color = PrimaryGreen,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentStep",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "STEP $currentStep",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "of $totalSteps steps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = PrimaryGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun StepContentCard(description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient Accent Line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PrimaryGreen,
                                PrimaryGreen.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "👨‍🍳",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = PrimaryGreen
                    )
                }
                
                // Description Text
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 17.sp,
                        lineHeight = 26.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun IngredientsSection(ingredients: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🥘",
                    fontSize = 24.sp
                )
                Text(
                    text = "Ingredients for this step",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Ingredients Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ingredients.forEach { ingredient ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryGreen.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            PrimaryGreen.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryGreen.copy(alpha = 0.2f),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓",
                                        fontSize = 12.sp,
                                        color = PrimaryGreen
                                    )
                                }
                            }
                            Text(
                                text = ingredient,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerCard(
    timeMinutes: Int,
    timerSeconds: Int,
    timerRunning: Boolean,
    timerFinished: Boolean,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    // Don't show timer if there's no time for this step
    if (timeMinutes <= 0) {
        return
    }
    
    // Show step time if timer hasn't started, otherwise show current timer value
    val displayMinutes = if (timerSeconds == 0 && !timerRunning) {
        timeMinutes
    } else {
        timerSeconds / 60
    }
    val displaySeconds = if (timerSeconds == 0 && !timerRunning) {
        0
    } else {
        timerSeconds % 60
    }
    val formattedTime = String.format("%02d:%02d", displayMinutes, displaySeconds)
    
    // Determine timer color based on progress
    val timerColor = when {
        timerFinished -> TimerRed
        timerRunning -> {
            val progress = timerSeconds.toFloat() / (timeMinutes * 60f)
            when {
                progress > 0.5f -> PrimaryGreen
                progress > 0.2f -> TimerAmber
                else -> TimerRed
            }
        }
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Timer Display with Circular Progress
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular Progress Ring
                val totalSeconds = timeMinutes * 60
                val progress = if (totalSeconds > 0) {
                    (totalSeconds - timerSeconds).toFloat() / totalSeconds.toFloat()
                } else {
                    0f
                }
                val sweepAngle = progress * 360f
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Background circle
                    drawCircle(
                        color = surfaceVariantColor.copy(alpha = 0.3f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    
                    // Progress circle
                    if (timerSeconds > 0 || timerRunning) {
                        drawArc(
                            color = timerColor,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
                
                // Timer Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = timerColor,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    if (timerRunning) {
                        Text(
                            text = "⏱️",
                            fontSize = 20.sp
                        )
                    }
                }
            }
            
            // Timer Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    timerSeconds == 0 && !timerRunning -> {
                        // Start button
                        Button(
                            onClick = onStartTimer,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("▶️", fontSize = 18.sp)
                                Text(
                                    text = "Start Timer",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 15.sp
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    timerRunning -> {
                        // Pause button
                        OutlinedButton(
                            onClick = onPauseTimer,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⏸️", fontSize = 18.sp)
                                Text(
                                    text = "Pause",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 15.sp
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    timerSeconds > 0 && !timerRunning -> {
                        // Resume and Reset buttons
                        OutlinedButton(
                            onClick = onResumeTimer,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryGreen
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("▶️", fontSize = 18.sp)
                                Text(
                                    text = "Resume",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 15.sp
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        
                        OutlinedButton(
                            onClick = onResetTimer,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⏹️", fontSize = 18.sp)
                                Text(
                                    text = "Reset",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 15.sp
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            
            // Timer finished message with pulsing animation
            if (timerFinished) {
                val infiniteTransition = rememberInfiniteTransition(label = "timer_finished")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )
                
                Text(
                    text = "⏰ Time's up!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = TimerRed.copy(alpha = alpha)
                )
            }
        }
    }
}

@Composable
private fun NavigationButtonsRow(
    modifier: Modifier = Modifier,
    currentStep: Int,
    totalSteps: Int,
    isFirstStep: Boolean,
    isLastStep: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Step Indicator Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalSteps) { index ->
                    val isActive = index + 1 == currentStep
                    Surface(
                        shape = CircleShape,
                        color = if (isActive) {
                            PrimaryGreen
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        },
                        modifier = Modifier.size(if (isActive) 8.dp else 6.dp)
                    ) {}
                    if (index < totalSteps - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
            
            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Previous Button
                OutlinedButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !isFirstStep,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("←", fontSize = 20.sp)
                        Text(
                            text = "Previous",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // Next/Finish Button
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLastStep) "Finish" else "Next Step",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!isLastStep) {
                            Text("→", fontSize = 20.sp)
                        } else {
                            Text("✓", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CookingCompleteScreen(
    recipeName: String,
    onNavigateToHome: () -> Unit,
    onAddToFavorites: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    
    // Multiple animation values for various elements
    val chefScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chef_scale"
    )
    
    val starRotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_rotation"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_offset"
    )
    
    val surfaceColor = MaterialTheme.colorScheme.surface
    val celebrationGold = Color(0xFFFFD700)
    val celebrationOrange = Color(0xFFFF9500)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryGreen.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background,
                        PrimaryGreen.copy(alpha = 0.08f)
                    )
                )
            )
    ) {
        // Animated Background Particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val particleColors = listOf(
                PrimaryGreen.copy(alpha = 0.15f),
                celebrationGold.copy(alpha = 0.2f),
                celebrationOrange.copy(alpha = 0.15f)
            )
            
            // Draw floating particles
            val particles = listOf(
                Triple(0.1f, 0.2f, 12f),
                Triple(0.85f, 0.15f, 8f),
                Triple(0.15f, 0.75f, 10f),
                Triple(0.9f, 0.8f, 14f),
                Triple(0.5f, 0.1f, 6f),
                Triple(0.3f, 0.9f, 8f),
                Triple(0.75f, 0.5f, 10f),
                Triple(0.05f, 0.5f, 12f),
                Triple(0.95f, 0.4f, 8f),
                Triple(0.4f, 0.85f, 6f)
            )
            
            particles.forEachIndexed { index, (xFraction, yFraction, baseRadius) ->
                val offsetMultiplier = if (index % 2 == 0) 1f else -1f
                val yOffset = particleOffset * offsetMultiplier
                
                drawCircle(
                    color = particleColors[index % particleColors.size],
                    radius = baseRadius + (particleOffset / 5f),
                    center = Offset(
                        x = size.width * xFraction,
                        y = size.height * yFraction + yOffset
                    )
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Success Badge with Glow Effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // Glow effect behind badge
                Surface(
                    shape = CircleShape,
                    color = PrimaryGreen.copy(alpha = glowAlpha * 0.3f),
                    modifier = Modifier.size(160.dp)
                ) {}
                
                Surface(
                    shape = CircleShape,
                    color = PrimaryGreen.copy(alpha = glowAlpha * 0.5f),
                    modifier = Modifier.size(140.dp)
                ) {}
                
                // Main badge
                Surface(
                    shape = CircleShape,
                    color = PrimaryGreen,
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "👨‍🍳",
                            fontSize = (56 * chefScale).sp
                        )
                    }
                }
                
                // Checkmark badge overlay
                Surface(
                    shape = CircleShape,
                    color = celebrationGold,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Animated Decorative Stars
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = (particleOffset / 3f).dp)
                ) {
                    Text("✨", fontSize = 28.sp)
                }
                Box(
                    modifier = Modifier
                        .offset(y = (-particleOffset / 4f).dp)
                ) {
                    Text("🎉", fontSize = 32.sp)
                }
                Canvas(
                    modifier = Modifier
                        .size(40.dp)
                        .offset(y = (particleOffset / 5f).dp)
                ) {
                    rotate(starRotation) {
                        drawCircle(
                            color = celebrationGold,
                            radius = 16f
                        )
                    }
                }
                Text("⭐", fontSize = 28.sp)
                Box(
                    modifier = Modifier
                        .offset(y = (particleOffset / 3f).dp)
                ) {
                    Text("🎊", fontSize = 32.sp)
                }
                Box(
                    modifier = Modifier
                        .offset(y = (-particleOffset / 4f).dp)
                ) {
                    Text("✨", fontSize = 28.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title with gradient-like effect
            Text(
                text = "Bon Appétit!",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = PrimaryGreen,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "You're a master chef!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                color = celebrationOrange,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Recipe Completion Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = surfaceColor,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("🍽️", fontSize = 16.sp)
                            }
                        }
                        Text(
                            text = "Successfully Cooked",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            ),
                            color = PrimaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Divider
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    
                    // Recipe Name
                    Text(
                        text = recipeName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    // Achievement Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Achievement Item 1
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏆", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Complete",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Achievement Item 2
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⭐", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "All Steps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Achievement Item 3
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💪", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Great Job",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Add to Favorites Button
            OutlinedButton(
                onClick = onAddToFavorites,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 2.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryGreen.copy(alpha = 0.6f),
                            celebrationGold.copy(alpha = 0.6f)
                        )
                    )
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("❤️", fontSize = 22.sp)
                    Text(
                        text = "Save to Favorites",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Back to Home Button with gradient-like effect
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏠", fontSize = 22.sp)
                    Text(
                        text = "Explore More Recipes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp
                        ),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
