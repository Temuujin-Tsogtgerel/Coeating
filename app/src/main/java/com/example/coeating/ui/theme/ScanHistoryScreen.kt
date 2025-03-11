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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coeating.ScanResult

/**
 * This screen displays a list of previous scan results. Each result
 * is shown in a card within a LazyColumn. When tapped, an AlertDialog
 * reveals more detailed information about the selected scan. If there
 * are no scans yet, a message is displayed to explain that no results
 * are available. The user can tap the “Close” button (styled to match
 * the Scan Ingredients button in the scanner screen) to dismiss the
 * dialog, or press the back arrow at the top to return to the previous
 * screen.
 */
@Composable
fun ScanHistory(
    previousScans: List<ScanResult>,
    onBack: () -> Unit
) {
    val selectedScan = remember { mutableStateOf<ScanResult?>(null) }

    Column {
        // Simple back button at the top
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }


        // If no previous scans, show explanation message; otherwise, show scans in LazyColumn
        if (previousScans.isEmpty()) {
            Text(
                text = "No scans available yet. Once you scan an item, you'll see it listed here.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                items(previousScans) { scan: ScanResult ->
                    Surface(
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                        color = Color(0xFF465B53),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { selectedScan.value = scan }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = scan.foodName,
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            val snippet = if (scan.details.length > 50) {
                                scan.details.substring(0, 50) + "..."
                            } else {
                                scan.details
                            }
                            Text(
                                text = snippet,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }

    // If a scan is selected, show details in an AlertDialog
    if (selectedScan.value != null) {
        AlertDialog(
            onDismissRequest = { selectedScan.value = null },
            title = { Text("Scan Details: ${selectedScan.value!!.foodName}") },
            text = { Text(selectedScan.value!!.details) },
            confirmButton = {
                // Styled to match the button from the scanner screen
                Button(
                    onClick = { selectedScan.value = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF8635E),
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }
            }
        )
    }
}
