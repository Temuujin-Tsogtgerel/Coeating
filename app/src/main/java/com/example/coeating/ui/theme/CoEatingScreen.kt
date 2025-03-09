package com.example.coeating.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoEatingScreen(
    onTakePhotoClick: () -> Unit,
    apiResult: String,
    overallScore: Boolean?,
    dietaryPreferences: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Co-Eating") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your dietary preferences: ${
                    if (dietaryPreferences.isNotBlank()) dietaryPreferences else "Not set"
                }",
                style = MaterialTheme.typography.titleMedium
            )
            Button(onClick = onTakePhotoClick) {
                Text(text = "Scan Ingredients")
            }
            // Display overall score if available.
            if (overallScore != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Overall Score: ", style = MaterialTheme.typography.titleMedium)
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
            if (apiResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Co-Eating says:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = apiResult,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
