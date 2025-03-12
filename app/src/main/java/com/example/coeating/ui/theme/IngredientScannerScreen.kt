package com.example.coeating.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun IngredientScannerScreen(
    onTakePhotoClick: () -> Unit,
    apiResult: String,
    dietaryPreferences: String,
    cosmeticPreferences: String,
    onChangePreferences: () -> Unit,
    onBack: () -> Unit,
    capturedBitmap: Bitmap? = null // Preview image passed from MainActivity
) {
    // Show steps only if there is no API result yet.
    val shouldShowSteps = apiResult.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back button at the top.
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        // Preferences card shows current dietary and cosmetic preferences.
        PreferencesCard(
            dietaryPreferences = dietaryPreferences,
            cosmeticPreferences = cosmeticPreferences,
            onChangePreferences = onChangePreferences
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show step instructions only if there is no API result.
        if (shouldShowSteps) {
            StepCard(
                stepNumber = 1,
                stepTitle = "Take Photo",
                stepDescription = "Capture the ingredient label.",
                icon = Icons.Filled.CameraAlt
            )
            Spacer(modifier = Modifier.height(8.dp))
            StepCard(
                stepNumber = 2,
                stepTitle = "Analyze",
                stepDescription = "Your image is being analyzed.",
                icon = Icons.Filled.CameraAlt
            )
            Spacer(modifier = Modifier.height(8.dp))
            StepCard(
                stepNumber = 3,
                stepTitle = "Result",
                stepDescription = "Results will appear below.",
                icon = Icons.Filled.CameraAlt
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display the captured image if available.
        capturedBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured Image Preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display the API result or loading indicator.
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
                        Spacer(modifier = Modifier.height(4.dp))
                        // Remove asterisks from API result text
                        Text(
                            text = apiResult.replace("*", ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Button to take a photo (scan ingredients).
        Button(
            onClick = { onTakePhotoClick() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF8635E),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Scan Ingredients"
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Step $stepNumber",
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dietary Preferences: ${if (dietaryPreferences.isNotBlank()) dietaryPreferences.replace("*", "") else "Not set"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cosmetic Preferences: ${if (cosmeticPreferences.isNotBlank()) cosmeticPreferences.replace("*", "") else "Not set"}",
                style = MaterialTheme.typography.bodyMedium
            )
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
