// File: app/src/main/java/com/example/coeating/ui/theme/PreferencesScreen.kt
package com.example.coeating.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A PreferencesScreen that displays the current saved preferences and
 * allows the user to update them. When the user taps the Back arrow or
 * Save button, the onBack callback is triggered with updated values.
 */
@Composable
fun PreferencesScreen(
    initialUserName: String = "",
    initialDietaryPreferences: String = "",
    initialCosmeticPreferences: String = "",
    onBack: (String, String, String) -> Unit
) {
    // Hold local state for each field.
    val updatedUserName = remember { mutableStateOf(initialUserName) }
    val updatedDietary = remember { mutableStateOf(initialDietaryPreferences) }
    val updatedCosmetic = remember { mutableStateOf(initialCosmeticPreferences) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Back button triggers onBack with current values.
        IconButton(onClick = {
            onBack(
                updatedUserName.value,
                updatedDietary.value,
                updatedCosmetic.value
            )
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = "Preferences",
            style = MaterialTheme.typography.titleLarge
        )
        // Show current values
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Name: ${if (initialUserName.isNotBlank()) initialUserName else "Not set"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dietary Preferences: ${if (initialDietaryPreferences.isNotBlank()) initialDietaryPreferences else "Not set"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cosmetic Preferences: ${if (initialCosmeticPreferences.isNotBlank()) initialCosmeticPreferences else "Not set"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // The update form for preferences.
        Text(
            text = "Update Preferences",
            style = MaterialTheme.typography.titleLarge
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = updatedUserName.value,
                    onValueChange = { updatedUserName.value = it },
                    label = { Text("Your name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = updatedDietary.value,
                    onValueChange = { updatedDietary.value = it },
                    label = { Text("Dietary Preferences") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = updatedCosmetic.value,
                    onValueChange = { updatedCosmetic.value = it },
                    label = { Text("Cosmetic Preferences") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Button to save the updated values.
        Button(
            onClick = {
                onBack(
                    updatedUserName.value,
                    updatedDietary.value,
                    updatedCosmetic.value
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Save Preferences")
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Text(text = "Save Preferences")
        }
    }
}