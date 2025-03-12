package com.example.coeating.ui.theme

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.coeating.ScanResult

@Composable
fun ScanCard(
    scan: ScanResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF465B53)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Show thumbnail if an image path exists.
            scan.imagePath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Scan image",
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            // Remove any asterisks from the text fields
            Text(
                text = "Scan ID: ${scan.id}",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = scan.name.replace("*", ""),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            val cleanDetails = if (scan.details.length > 50)
                scan.details.substring(0, 50).replace("*", "") + "..."
            else
                scan.details.replace("*", "")
            Text(
                text = cleanDetails,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ScanHistory(
    previousScans: List<ScanResult>,
    onDeleteScan: (ScanResult) -> Unit,
    onBack: () -> Unit
) {
    val selectedScan = remember { mutableStateOf<ScanResult?>(null) }

    if (selectedScan.value != null) {
        ScanDetailScreen(
            scan = selectedScan.value!!,
            onDelete = {
                onDeleteScan(selectedScan.value!!)
                selectedScan.value = null
            },
            onBack = { selectedScan.value = null }
        )
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            if (previousScans.isEmpty()) {
                Text(
                    text = "No scans available yet. Once you scan an item, you'll see it listed here.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Text(
                    text = "Your scan history is listed below. Each scan card shows its unique ID and a brief overview of the scan details. Tap a card to view full details.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(previousScans) { scan ->
                        ScanCard(scan = scan, onClick = { selectedScan.value = scan })
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
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        // Show a larger preview if available.
        scan.imagePath?.let { path ->
            val bitmap = BitmapFactory.decodeFile(path)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Scan image preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF465B53)),
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
                    text = "Scan ID Number: ${scan.id}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Food: ${scan.name.replace("*", "")}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scan.details.replace("*", ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
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
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Delete Scan")
        }
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
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Go Back")
        }
    }
}
