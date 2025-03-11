// file: app/src/main/java/com/example/coeating/ui/theme/PreferencesScreen.kt
package com.example.coeating.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coeating.ui.components.LargeBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    initialUserName: String,
    initialDietaryPreferences: String,
    initialCosmeticPreferences: String,
    onBack: (String, String, String) -> Unit
) {
    // Create state holders for user input
    val userNameText = remember { mutableStateOf(initialUserName) }
    val dietaryText = remember { mutableStateOf(initialDietaryPreferences) }
    val cosmeticText = remember { mutableStateOf(initialCosmeticPreferences) }

    // Use Material3 Scaffold for the screen layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferences") },
                navigationIcon = {
                    IconButton(onClick = {
                        // When the back icon is tapped, pass back the current values
                        onBack(userNameText.value, dietaryText.value, cosmeticText.value)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Input field for user name
            TextField(
                value = userNameText.value,
                onValueChange = { userNameText.value = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )
            // Input field for dietary preferences
            TextField(
                value = dietaryText.value,
                onValueChange = { dietaryText.value = it },
                label = { Text("Dietary Preferences ie. Paleo, Keto, Vegan, Halal, Wholefoods ...") },
                modifier = Modifier.fillMaxWidth()
            )
            // Input field for cosmetic preferences
            TextField(
                value = cosmeticText.value,
                onValueChange = { cosmeticText.value = it },
                label = { Text("Cosmetic Preferences: ie. No Sulfates, only essential oils as fragrance, no petroleum derived ingredients") },
                modifier = Modifier.fillMaxWidth()
            )
            // A button-like block to save and confirm preferences
            LargeBlock(
                title = "Save Preferences",
                subtext = "Confirm your settings",
                icon = Icons.Default.Settings,
                backgroundColor = Color(0xFFF8635E),
                onClick = { onBack(userNameText.value, dietaryText.value, cosmeticText.value) }
            )
        }
    }
}
