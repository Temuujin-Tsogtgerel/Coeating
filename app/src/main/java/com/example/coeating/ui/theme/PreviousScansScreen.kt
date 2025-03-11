package com.example.coeating.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coeating.ScanResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousScansScreen(
    previousScans: List<ScanResult>,
    onBack: () -> Unit
) {
    val selectedScan = remember { mutableStateOf<ScanResult?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Previous Scans") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(innerPadding)
        ) {
            items(previousScans) { scan: ScanResult ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFF465B53),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { selectedScan.value = scan }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = scan.foodName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        val snippet = if (scan.details.length > 50) {
                            scan.details.substring(0, 50) + "..."
                        } else {
                            scan.details
                        }
                        Text(
                            text = snippet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
    if (selectedScan.value != null) {
        AlertDialog(
            onDismissRequest = { selectedScan.value = null },
            title = { Text("Scan Details: ${selectedScan.value!!.foodName}") },
            text = { Text(selectedScan.value!!.details) },
            confirmButton = {
                Button(onClick = { selectedScan.value = null }) {
                    Text("Close")
                }
            }
        )
    }
}
