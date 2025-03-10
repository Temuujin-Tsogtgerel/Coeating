package com.example.coeating.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * The main CoEating screen for scanning ingredients and displaying results.
 *
 * @param onTakePhotoClick Callback for when the user taps "Scan Ingredients."
 * @param apiResult The textual response from the API after scanning.
 * @param overallScore Boolean indicating a pass/fail score for the ingredients (e.g., "friendly" or not).
 * @param dietaryPreferences A string of the user’s dietary preferences (e.g., "Keto, Vegan").
 * @param onChangePreferences Callback for when the user selects "Change Preferences" in the menu.
 * @param onShowPreviousScans Callback for when the user selects "Previous Scans" in the menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoEatingScreen(
    onTakePhotoClick: () -> Unit,
    apiResult: String,
    overallScore: Boolean?,
    dietaryPreferences: String,
    onChangePreferences: () -> Unit,
    onShowPreviousScans: () -> Unit
) {
    // Controls the overflow menu state.
    val menuExpanded = remember { mutableStateOf(false) }

    // A simple gradient background (customize as needed).
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Co-Eating", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { menuExpanded.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Change Preferences") },
                            onClick = {
                                menuExpanded.value = false
                                onChangePreferences()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Previous Scans") },
                            onClick = {
                                menuExpanded.value = false
                                onShowPreviousScans()
                            }
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Instructions for the user.
                Text(
                    text = "Take a clear photo of your ingredients to check if they fit your dietary preferences.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Display current dietary preferences.
                Text(
                    text = "Your dietary preferences: ${
                        if (dietaryPreferences.isNotBlank()) dietaryPreferences else "Not set"
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Button to initiate ingredient scanning.
                Button(
                    onClick = onTakePhotoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    Text(text = "Scan Ingredients")
                }

                // If the app is currently loading a response, show a progress indicator.
                if (apiResult == "Loading...") {
                    CircularProgressIndicator()
                }

                // If overallScore is available, display pass/fail icons.
                if (overallScore != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Overall Score: ",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (overallScore) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Pass",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Fail",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Display the API response if it's not empty or "Loading..."
                if (apiResult.isNotEmpty() && apiResult != "Loading...") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Co-Eating says:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = apiResult,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
