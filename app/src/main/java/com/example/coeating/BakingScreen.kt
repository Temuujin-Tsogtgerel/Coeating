package com.example.coeating.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coeating.ui.components.LargeBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakingScreen(
    onTakePhotoClick: () -> Unit,
    apiResult: String,
    isKetoSelected: Boolean,
    onToggleKeto: () -> Unit,
    isPaleoSelected: Boolean,
    onTogglePaleo: () -> Unit,
    isVeganSelected: Boolean,
    onToggleVegan: () -> Unit,
    isHalalSelected: Boolean,
    onToggleHalal: () -> Unit,
    isWholeFoodsSelected: Boolean,
    onToggleWholeFoods: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Baking Screen") },
                navigationIcon = {
                    IconButton(onClick = { /* Provide back action if needed */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isKetoSelected, onCheckedChange = { onToggleKeto() })
                Text(text = "Keto", style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isPaleoSelected, onCheckedChange = { onTogglePaleo() })
                Text(text = "Paleo", style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isVeganSelected, onCheckedChange = { onToggleVegan() })
                Text(text = "Vegan", style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isHalalSelected, onCheckedChange = { onToggleHalal() })
                Text(text = "Halal", style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isWholeFoodsSelected, onCheckedChange = { onToggleWholeFoods() })
                Text(text = "Whole Foods", style = MaterialTheme.typography.bodyMedium)
            }
            LargeBlock(
                title = "Take Photo",
                subtext = "Capture an image",
                icon = Icons.Filled.CameraAlt,
                backgroundColor = Color(0xFF25302C),
                onClick = onTakePhotoClick
            )
            if (apiResult.isNotEmpty()) {
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
        }
    }
}
