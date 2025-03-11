// file: app/src/main/java/com/example/coeating/ui/CoEatingScreen.kt
package com.example.coeating.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coeating.ui.components.LargeBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoEatingScreen(
    onTakePhotoClick: () -> Unit,
    apiResult: String,
    overallScore: Boolean?, // if needed for future use
    dietaryPreferences: String,
    cosmeticPreferences: String,
    onChangePreferences: () -> Unit,
    onShowPreviousScans: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredients Scanner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Display current preferences
            Text(
                text = "Dietary: ${if (dietaryPreferences.isNotBlank()) dietaryPreferences else "Not set"}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Cosmetic: ${if (cosmeticPreferences.isNotBlank()) cosmeticPreferences else "Not set"}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Block to initiate a scan
            LargeBlock(
                title = "Scan Ingredients",
                subtext = "Tap to take a photo",
                icon = Icons.Filled.CameraAlt,
                backgroundColor = Color(0xFF25302C),
                onClick = onTakePhotoClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Show API response or a loading indicator
            if (apiResult == "Loading...") {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            } else if (apiResult.isNotEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFF465B53),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
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
            Spacer(modifier = Modifier.height(16.dp))
            // Block for updating preferences
            LargeBlock(
                title = "Preferences",
                subtext = "Update your settings",
                icon = Icons.Filled.Settings,
                backgroundColor = Color(0xFFF8635E),
                onClick = onChangePreferences
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Block for viewing previous scans
            LargeBlock(
                title = "Previous Scans",
                subtext = "View past results",
                icon = Icons.Filled.History,
                backgroundColor = Color(0xFF465B53),
                onClick = onShowPreviousScans
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoEatingScreenPreview() {
    CoEatingScreen(
        onTakePhotoClick = {},
        apiResult = "This is a preview result.",
        overallScore = null,
        dietaryPreferences = "Vegan",
        cosmeticPreferences = "None",
        onChangePreferences = {},
        onShowPreviousScans = {},
        onBack = {}
    )
}
