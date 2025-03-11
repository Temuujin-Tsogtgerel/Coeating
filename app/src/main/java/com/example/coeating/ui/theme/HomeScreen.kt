// file: app/src/main/java/com/example/coeating/ui/theme/HomeScreen.kt
package com.example.coeating.ui.theme

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coeating.ui.components.LargeBlock
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String = "John",
    onScanClick: () -> Unit = {}, // Added parameter
    onPreviousScansClick: () -> Unit = {},
    onPreferencesClick: () -> Unit = {},
    onWorkClick: () -> Unit = {},
    onFriendsClick: () -> Unit = {},
    onSocialMediaClick: () -> Unit = {}
) {
    // Create a launcher to take a picture preview using the camera.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        // Handle the captured image bitmap here.
        // For example, you might navigate to a screen displaying the captured image.
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer header
                Text(
                    text = "Navigation Drawer",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                // Drawer Items
                NavigationDrawerItem(
                    label = { Text("Scan") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Launch the camera immediately.
                        cameraLauncher.launch()
                        onScanClick()
                    },
                    icon = { Icon(Icons.Filled.CameraAlt, contentDescription = "Scan") },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Previous Scans") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onPreviousScansClick()
                    },
                    icon = { Icon(Icons.Filled.History, contentDescription = "Previous Scans") },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Preferences") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onPreferencesClick()
                    },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Preferences") },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Hi, $userName") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = onWorkClick,
                        icon = { Icon(Icons.Filled.CameraAlt, contentDescription = "Work") },
                        label = { Text("Work") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onFriendsClick,
                        icon = { Icon(Icons.Filled.History, contentDescription = "Friends") },
                        label = { Text("Friends") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onSocialMediaClick,
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Social") },
                        label = { Text("Social") }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Scan an item's ingredients label to verify it aligns with your diet and cosmetic preferences.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                // When clicking the "Scan" block, the cameraLauncher is invoked.
                LargeBlock(
                    title = "Scan",
                    subtext = "Scan ingredients",
                    icon = Icons.Filled.CameraAlt,
                    backgroundColor = Color(0xFF25302C),
                    onClick = {
                        cameraLauncher.launch()
                        onScanClick()
                    }
                )
                LargeBlock(
                    title = "Previous Scans",
                    subtext = "Review past scans",
                    icon = Icons.Filled.History,
                    backgroundColor = Color(0xFF465B53),
                    onClick = onPreviousScansClick
                )
                LargeBlock(
                    title = "Preferences",
                    subtext = "Set dietary and cosmetics preferences",
                    icon = Icons.Filled.Settings,
                    backgroundColor = Color(0xFFF8635E),
                    onClick = onPreferencesClick
                )
            }
        }
    }
}
