// file: app/src/main/java/com/example/coeating/ui/theme/ScanHistoryScreen.kt
package com.example.coeating.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coeating.ScanResult

@Composable
fun ScanHistory(
    previousScans: List<ScanResult>,
    onDeleteScan: (ScanResult) -> Unit,
    onBack: () -> Unit
) {
    var selectedScan by remember { mutableStateOf<ScanResult?>(null) }

    if (selectedScan != null) {
        // Detailed view with a delete option.
        ScanDetailScreen(
            scan = selectedScan!!,
            onDelete = {
                onDeleteScan(selectedScan!!)
                selectedScan = null
            },
            onBack = { selectedScan = null }
        )
    } else {
        // List view of all scans.
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Back Button
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            if (previousScans.isEmpty()) {
                Text(
                    text = "No scans available yet. Once you scan an item, you'll see it listed here.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(previousScans) { scan ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = Color(0xFF465B53),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { selectedScan = scan }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = scan.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                val snippet = if (scan.details.length > 50)
                                    scan.details.substring(0, 50) + "..."
                                else
                                    scan.details
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
        }
    }
}

@Composable
fun ScanDetailScreen(
    scan: ScanResult,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Back button to return to the list view.
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }
        // Display scan details inside a Card.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Scan Details:",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Food: ${scan.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scan.details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        // Delete button that triggers the delete callback.
        Button(
            onClick = onDelete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete Scan",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Delete Scan")
        }
        // "Go Back" button for users who do not wish to delete.
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF8635E),
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Go Back")
        }
    }
}
