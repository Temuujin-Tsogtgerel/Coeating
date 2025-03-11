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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Holds the captured image as a Bitmap
    private var capturedImage = mutableStateOf<Bitmap?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register camera intent launcher
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    capturedImage.value = bitmap
                }
            }

        // Register permission request launcher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    dispatchTakePictureIntent()
                }
            }

        setContent {
            CoeatingTheme {
                val bakingViewModel: BakingViewModel = viewModel()
                val currentUiState = bakingViewModel.uiState.collectAsState().value

                // App-wide user preferences
                var userName by remember { mutableStateOf("") }
                var dietaryPreferences by remember { mutableStateOf("") }
                var cosmeticPreferences by remember { mutableStateOf("") }

                // Track which screen is displayed
                var currentScreen by remember { mutableStateOf("Home") }

                // If user just captured an image, send it to the model
                LaunchedEffect(capturedImage.value) {
                    capturedImage.value?.let { bitmap ->
                        val prompt = "Check ingredients. If it's food, does it fit " +
                                "$dietaryPreferences? If it's cosmetics, does it fit $cosmeticPreferences?"
                        bakingViewModel.sendPrompt(bitmap, prompt)
                        // Reset after sending
                        capturedImage.value = null
                    }
                }

                // If no user name yet, force them to set preferences at start
                LaunchedEffect(Unit) {
                    if (userName.isBlank()) {
                        currentScreen = "Preferences"
                    }
                }

                // Drawer state
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // Side Drawer + Scaffold
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(
                                text = "More Features",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                            // Completely different dummy options below:
                            NavigationDrawerItem(
                                label = { Text("Explore Partners") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    // Example: no special screen yet, so do nothing or
                                    // set currentScreen to a dummy value like "ExplorePartners"
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Explore Partners"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Subscription Plans") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    // Example: set currentScreen = "SubscriptionPlans"
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Subscription Plans"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Recipe Guides") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    // Example: set currentScreen = "RecipeGuides"
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Recipe Guides"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Contact Support") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    // Example: set currentScreen = "ContactSupport"
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Contact Support"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                ) {
                    // TOP-LEVEL Scaffold that includes a Bottom Bar
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("CoEating - $currentScreen") },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            // Bottom navigation
                            NavigationBar {
                                // Home item
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
                                // Ingredient Scanner item
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
                                // Preferences
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
                            // Main content: pick composable based on currentScreen
                            when (currentScreen) {
                                "Home" -> HomeScreen(
                                    userName = userName,
                                    onScanClick = { currentScreen = "IngredientScanner" },
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
                                    overallScore = if (currentUiState is UiState.Success) currentUiState.overallScore else null,
                                    dietaryPreferences = dietaryPreferences,
                                    cosmeticPreferences = cosmeticPreferences,
                                    onChangePreferences = { currentScreen = "Preferences" },
                                    onShowPreviousScans = { currentScreen = "ScanHistory" },
                                    onBack = { currentScreen = "Home" }
                                )
                                "ScanHistory" -> ScanHistory(
                                    previousScans = bakingViewModel.previousScans,
                                    onBack = { currentScreen = "Home" }
                                )
                                "Preferences" -> PreferencesScreen(
                                    initialUserName = userName,
                                    initialDietaryPreferences = dietaryPreferences,
                                    initialCosmeticPreferences = cosmeticPreferences
                                ) { newName, newDiet, newCosmetic ->
                                    userName = newName
                                    dietaryPreferences = newDiet
                                    cosmeticPreferences = newCosmetic
                                    currentScreen = "Home"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if we have Camera permission; if yes, proceed to take photo.
     * Otherwise, request permission.
     */
    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Creates an Intent to capture a photo into a File/Uri.
     */
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

    /**
     * Creates a temporary image file in the app's picture directory.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
//MainActivity.kt