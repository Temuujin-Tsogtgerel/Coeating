package com.example.coeating

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun PreferencesDialog(
    initialText: String,
    onSave: (String) -> Unit
) {
    var inputText by remember { mutableStateOf(initialText) }
    AlertDialog(
        onDismissRequest = { /* Prevent dismiss without saving */ },
        title = { Text(text = "Enter Dietary Preferences") },
        text = {
            OutlinedTextField(
                value = inputText,
                onValueChange = { newText -> inputText = newText },
                label = { Text("e.g., keto, paleo, vegan, halal, whole foods") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(inputText) }) {
                Text("Save")
            }
        }
    )
}