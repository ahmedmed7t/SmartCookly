package com.nexable.smartcookly.feature.onboarding.presentation.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.onboarding.data.model.Disease
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableDiseaseCard
import org.jetbrains.compose.resources.painterResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_check

@Composable
fun DiseaseSelectionStep(
    selectedDiseases: Set<Disease>,
    otherDiseaseText: String,
    showOtherTextField: Boolean,
    onDiseaseToggle: (Disease) -> Unit,
    onOtherTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title with emoji
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "💚", fontSize = 28.sp)
            Text(
                text = "Health Conditions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Select any health conditions so we can tailor recipes to your dietary needs.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Disease list
        val diseasesWithoutNothing = Disease.entries.filter { it != Disease.NOTHING }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nothing option - displayed prominently at the top
            item {
                val isNothingSelected = selectedDiseases.contains(Disease.NOTHING)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDiseaseToggle(Disease.NOTHING) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNothingSelected) 
                            MaterialTheme.colorScheme.secondaryContainer 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        width = if (isNothingSelected) 2.dp else 1.dp,
                        color = if (isNothingSelected) 
                            MaterialTheme.colorScheme.secondary 
                        else 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = Disease.NOTHING.emoji,
                                    fontSize = 24.sp
                                )
                            }
                            Column {
                                Text(
                                    text = Disease.NOTHING.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = Disease.NOTHING.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isNothingSelected) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isNothingSelected) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_check),
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(diseasesWithoutNothing) { disease ->
                SelectableDiseaseCard(
                    disease = disease,
                    isSelected = selectedDiseases.contains(disease),
                    onClick = { onDiseaseToggle(disease) }
                )
            }

            // Other text field
            if (showOtherTextField) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otherDiseaseText,
                        onValueChange = onOtherTextChange,
                        label = { Text("Specify other health condition") },
                        placeholder = { Text("e.g., Thyroid, Arthritis...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            focusedLabelColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
