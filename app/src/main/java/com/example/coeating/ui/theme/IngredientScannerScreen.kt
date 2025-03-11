package com.example.coeating.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun IngredientScannerScreen(
    onTakePhotoClick: () -> Unit,
    apiResult: String,
    overallScore: Boolean?,
    dietaryPreferences: String,
    cosmeticPreferences: String,
    onChangePreferences: () -> Unit,
    onShowPreviousScans: () -> Unit,
    onBack: () -> Unit
) {
    // State to track if steps should be visible
    var showSteps by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Back button at the top
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        // Designed rectangle for Preferences
        PreferencesCard(
            dietaryPreferences = dietaryPreferences,
            cosmeticPreferences = cosmeticPreferences,
            onChangePreferences = onChangePreferences
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Conditionally show steps only when showSteps is true
        if (showSteps) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StepCard(
                    stepNumber = 1,
                    stepTitle = "Take Photo",
                    stepDescription = "We’ll read the ingredients and nutrition label from your photo.",
                    icon = Icons.Filled.CameraAlt
                )
                StepCard(
                    stepNumber = 2,
                    stepTitle = "Analyze",
                    stepDescription = "Our system analyzes the ingredients against your dietary and cosmetic preferences",
                    icon = Icons.Filled.CameraAlt // Replace with a more appropriate icon if desired
                )
                StepCard(
                    stepNumber = 3,
                    stepTitle = "Result",
                    stepDescription = "You’ll see a summary and recommendation below.",
                    icon = Icons.Filled.CameraAlt // Replace with a more appropriate icon if desired
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Show a loading indicator or API result if available
        when {
            apiResult == "Loading..." -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
            apiResult.isNotEmpty() -> {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFF465B53),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Co-Eating says:",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = apiResult,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // "Scan Ingredients" button
        Button(
            onClick = {
                // Hide steps and then trigger the scan action
                showSteps = false
                onTakePhotoClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF8635E),
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Filled.CameraAlt,
                contentDescription = "Camera Icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Scan Ingredients",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Tap to take a photo",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StepCard(
    stepNumber: Int,
    stepTitle: String,
    stepDescription: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = "Step $stepNumber icon",
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Step $stepNumber: $stepTitle",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stepDescription,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PreferencesCard(
    dietaryPreferences: String,
    cosmeticPreferences: String,
    onChangePreferences: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        // You can adjust the elevation and background color as desired
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dietary Preferences: ${if (dietaryPreferences.isNotBlank()) dietaryPreferences else "Not set"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cosmetic Preferences: ${if (cosmeticPreferences.isNotBlank()) cosmeticPreferences else "Not set"}",
                style = MaterialTheme.typography.bodyMedium
            )
            // If either preference is not set, prompt the user to set preferences
            if (dietaryPreferences.isBlank() || cosmeticPreferences.isBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Set in Preferences?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3F51B5),
                    modifier = Modifier.clickable { onChangePreferences() }
                )
            }
        }
    }
}
