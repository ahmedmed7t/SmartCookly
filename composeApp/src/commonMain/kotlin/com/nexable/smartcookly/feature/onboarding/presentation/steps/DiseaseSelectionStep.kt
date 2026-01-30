package com.nexable.smartcookly.feature.onboarding.presentation.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexable.smartcookly.feature.onboarding.data.model.Disease
import com.nexable.smartcookly.feature.onboarding.presentation.components.SelectableDiseaseCard

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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Disease.entries) { disease ->
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
