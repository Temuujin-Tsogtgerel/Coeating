// File: app/src/main/java/com/example/coeating/MainActivity.kt
package com.example.coeating

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coeating.ui.theme.CoeatingTheme
import com.example.coeating.ui.theme.HomeScreen
import com.example.coeating.ui.theme.IngredientScannerScreen
import com.example.coeating.ui.theme.PreferencesScreen
import com.example.coeating.ui.theme.ScanHistory
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var photoUri: Uri
    private lateinit var photoFile: File

    // Holds the captured image as a Bitmap.
    private var capturedImage = mutableStateOf<Bitmap?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the camera intent launcher.
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                capturedImage.value = bitmap
            }
        }

        // Register the permission request launcher.
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            }
        }

        // Create an instance of PreferencesRepository.
        val preferencesRepository = PreferencesRepository(this)

        setContent {
            CoeatingTheme {
                // Obtain the BakingViewModel.
                val bakingViewModel: BakingViewModel = viewModel()
                val currentUiState = bakingViewModel.uiState.collectAsState().value

                // Collect persistent preferences.
                val userName by produceState(initialValue = "") {
                    preferencesRepository.userNameFlow.collect { value = it }
                }
                val dietaryPreferences by produceState(initialValue = "") {
                    preferencesRepository.dietaryPreferencesFlow.collect { value = it }
                }
                val cosmeticPreferences by produceState(initialValue = "") {
                    preferencesRepository.cosmeticPreferencesFlow.collect { value = it }
                }

                // Track the current screen.
                var currentScreen by remember { mutableStateOf("Home") }
                // New state to hold the last scanned image for display.
                var lastScannedBitmap by remember { mutableStateOf<Bitmap?>(null) }
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                // Process the captured image.
                LaunchedEffect(capturedImage.value) {
                    capturedImage.value?.let { bitmap ->
                        // Save the captured bitmap to display later.
                        lastScannedBitmap = bitmap
                        val imagePath = photoFile.absolutePath
                        val prompt = """
                            Analyze the ingredients to determine the product type and what the product is.
                            Return a single text string indicating the product type.
                            If the product is identified as food or supplement, evaluate whether it meets the $dietaryPreferences.
                            Otherwise, if it is identified as a cosmetic, assess whether it aligns with the $cosmeticPreferences.
                            Break your findings into clearly labeled sections with headings.
                        """.trimIndent()
                        bakingViewModel.sendPrompt(bitmap, prompt, imagePath)
                        // Clear capturedImage so subsequent scans work.
                        capturedImage.value = null
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(
                                text = "Personalized Ingredient Scanner",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                            // Additional drawer items can be added here.
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(currentScreen) },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = (currentScreen == "Home"),
                                    onClick = { currentScreen = "Home" },
                                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                                    label = {
                                        Text(
                                            "Home",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                )
                                NavigationBarItem(
                                    selected = (currentScreen == "IngredientsScanner"),
                                    onClick = { currentScreen = "IngredientsScanner" },
                                    icon = { Icon(Icons.Filled.CameraAlt, contentDescription = "Ingredient Scanner") },
                                    label = {
                                        Text(
                                            "Ingredient Scanner",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                )
                                NavigationBarItem(
                                    selected = (currentScreen == "ScanHistory"),
                                    onClick = { currentScreen = "ScanHistory" },
                                    icon = { Icon(Icons.Filled.History, contentDescription = "Scan History") },
                                    label = {
                                        Text(
                                            "Scan History",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                )
                                NavigationBarItem(
                                    selected = (currentScreen == "Preferences"),
                                    onClick = { currentScreen = "Preferences" },
                                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Preferences") },
                                    label = {
                                        Text(
                                            "Preferences",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentScreen) {
                                "Home" -> HomeScreen(
                                    userName = userName,
                                    onScanClick = { currentScreen = "IngredientsScanner" },
                                    onPreviousScansClick = { currentScreen = "ScanHistory" },
                                    onPreferencesClick = { currentScreen = "Preferences" }
                                )
                                "IngredientsScanner" -> IngredientScannerScreen(
                                    onTakePhotoClick = { checkCameraPermissionAndTakePhoto() },
                                    apiResult = when (currentUiState) {
                                        is UiState.Success -> currentUiState.output
                                        is UiState.Error -> "Error: ${currentUiState.message}"
                                        is UiState.Loading -> "Loading..."
                                        else -> ""
                                    },
                                    dietaryPreferences = dietaryPreferences,
                                    cosmeticPreferences = cosmeticPreferences,
                                    onChangePreferences = { currentScreen = "Preferences" },
                                    onBack = { currentScreen = "Home" },
                                    capturedBitmap = lastScannedBitmap
                                )
                                "ScanHistory" -> ScanHistory(
                                    previousScans = bakingViewModel.previousScans,
                                    onDeleteScan = { scan -> bakingViewModel.deleteScan(scan) },
                                    onBack = { currentScreen = "Home" }
                                )
                                "Preferences" -> PreferencesScreen(
                                    initialUserName = userName,
                                    initialDietaryPreferences = dietaryPreferences,
                                    initialCosmeticPreferences = cosmeticPreferences
                                ) { newName, newDiet, newCosmetic ->
                                    scope.launch {
                                        preferencesRepository.savePreferences(newName, newDiet, newCosmetic)
                                    }
                                    currentScreen = "Home"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun dispatchTakePictureIntent() {
        try {
            photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                this,
                "com.example.coeating.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(photoUri)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
