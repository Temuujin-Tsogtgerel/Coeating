package com.example.coeating.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun DefaultBottomNavigation(
    currentScreen: String,
    onHomeClick: () -> Unit,
    onScanClick: () -> Unit,
    onPreviousScansClick: () -> Unit,
    onPreferencesClick: () -> Unit
) {
    NavigationBar {
        // Home
        NavigationBarItem(
            selected = (currentScreen == "Home"),
            onClick = onHomeClick,
            icon = { androidx.compose.material3.Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = {
                Text(
                    "Home",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        )
        // Ingredient Scanner
        NavigationBarItem(
            selected = (currentScreen == "IngredientsScanner"),
            onClick = onScanClick,
            icon = { androidx.compose.material3.Icon(Icons.Filled.CameraAlt, contentDescription = "Scanner") },
            label = {
                Text(
                    "Scanner",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        )
        // Previous Scans
        NavigationBarItem(
            selected = (currentScreen == "Scan History"),
            onClick = onPreviousScansClick,
            icon = { androidx.compose.material3.Icon(Icons.Filled.History, contentDescription = "History") },
            label = {
                Text(
                    "History",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        )
        // Preferences
        NavigationBarItem(
            selected = (currentScreen == "Preferences"),
            onClick = onPreferencesClick,
            icon = { androidx.compose.material3.Icon(Icons.Filled.Settings, contentDescription = "Preferences") },
            label = {
                Text(
                    "Prefs",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}
