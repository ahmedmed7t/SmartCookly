package com.nexable.smartcookly.feature.onboarding.presentation.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        // Title
        Text(
            text = "Health Conditions",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle
        Text(
            text = "Select any health conditions so we can tailor recipes to your dietary needs.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Disease list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            items(Disease.entries) { disease ->
                SelectableDiseaseCard(
                    disease = disease,
                    isSelected = selectedDiseases.contains(disease),
                    onClick = { onDiseaseToggle(disease) }
                )
            }

            item {
                // Other text field (shown when Other is selected)
                if (showOtherTextField) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otherDiseaseText,
                        onValueChange = onOtherTextChange,
                        label = { Text("Specify other health condition") },
                        placeholder = { Text("e.g., Thyroid, Arthritis...") },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
