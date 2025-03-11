// file: app/src/main/java/com/example/coeating/ui/theme/HomeScreen.kt
package com.example.coeating.ui.theme

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coeating.ui.components.LargeBlock

@Composable
fun HomeScreen(
    userName: String = "John",
    onScanClick: () -> Unit = {},
    onPreviousScansClick: () -> Unit = {},
    onPreferencesClick: () -> Unit = {}
) {
    // Create a launcher to take a picture preview using the camera (optional).
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        // Handle the captured image bitmap here if needed
    }

    // Simply show top-level content (no Scaffold or bottom navigation).
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // A welcome message
        androidx.compose.material3.Text(
            text = "Hi, $userName",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LargeBlock(
            title = "Ingredient Scanner",
            subtext = "Scan an item's ingredients label to verify it fits with your dietary and cosmetic preferences.",
            icon = Icons.Filled.CameraAlt,
            backgroundColor = Color(0xFF25302C), // Dark green
            onClick = onScanClick
        )

        LargeBlock(
            title = "Scan History",
            subtext = "Review past scans",
            icon = Icons.Filled.History,
            backgroundColor = Color(0xFF465B53), // Muted green/gray
            onClick = onPreviousScansClick
        )

        // Preferences box with a new teal color
        LargeBlock(
            title = "Preferences",
            subtext = "Set dietary and cosmetics preferences",
            icon = Icons.Filled.Settings,
            backgroundColor = Color(0xFF2A9D8F), // Updated color
            onClick = onPreferencesClick
        )
    }
}
