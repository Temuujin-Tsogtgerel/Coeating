package com.example.coeating.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PreferencesScreen(
    initialUserName: String = "Jane Doe",
    initialDietaryPreferences: String = "Whole foods, Low sugar",
    initialCosmeticPreferences: String = "Natural ingredients, No petroleum derived ingredients",
    onBack: (String, String, String) -> Unit
) {
    val userNameText = remember { mutableStateOf(initialUserName) }
    val dietaryText = remember { mutableStateOf(initialDietaryPreferences) }
    val cosmeticText = remember { mutableStateOf(initialCosmeticPreferences) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Back button at the top
        IconButton(
            onClick = {
                onBack(
                    userNameText.value,
                    dietaryText.value,
                    cosmeticText.value
                )
            }
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = "Preferences",
            style = MaterialTheme.typography.titleLarge
        )

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
            label = { Text("Dietary Preferences: e.g., Paleo, Keto, Vegan") },
            modifier = Modifier.fillMaxWidth()
        )

        // Input field for cosmetic preferences
        TextField(
            value = cosmeticText.value,
            onValueChange = { cosmeticText.value = it },
            label = { Text("Cosmetic Preferences: e.g., Natural ingredients") },
            modifier = Modifier.fillMaxWidth()
        )

        // Button with icon & text centered
        Button(
            onClick = {
                onBack(
                    userNameText.value,
                    dietaryText.value,
                    cosmeticText.value
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF8635E), // Button color
                contentColor = Color.White          // Text/icon color
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Save Preferences",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Confirm your settings",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
