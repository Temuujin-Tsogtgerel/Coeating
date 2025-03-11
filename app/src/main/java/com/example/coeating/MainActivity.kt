package com.example.coeating

// New icon imports for updated drawer options.
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.NavigationDrawerItem
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

        // Create an instance of PreferencesRepository for persistent preferences.
        val preferencesRepository = PreferencesRepository(this)

        setContent {
            CoeatingTheme {
                // Obtain the BakingViewModel (which now persists scans via Room).
                val bakingViewModel: BakingViewModel = viewModel()
                val currentUiState = bakingViewModel.uiState.collectAsState().value

                // Collect persistent preferences from DataStore.
                val userName by produceState<String>(initialValue = "") {
                    preferencesRepository.userNameFlow.collect { value = it }
                }
                val dietaryPreferences by produceState<String>(initialValue = "") {
                    preferencesRepository.dietaryPreferencesFlow.collect { value = it }
                }
                val cosmeticPreferences by produceState<String>(initialValue = "") {
                    preferencesRepository.cosmeticPreferencesFlow.collect { value = it }
                }

                // Track the current screen; default is "Home".
                var currentScreen by remember { mutableStateOf("Home") }

                // Process the captured image with a prompt using user preferences.
                LaunchedEffect(capturedImage.value) {
                    capturedImage.value?.let { bitmap ->
                        val prompt = "Analyze the ingredients to determine the product type and what the product is. " +
                                "If the product is identified as food or supplement, evaluate whether it meets the $dietaryPreferences. " +
                                "If it is identified as a cosmetic, assess whether it aligns with the $cosmeticPreferences. " +
                                "Present your findings in bullet points."
                        bakingViewModel.sendPrompt(bitmap, prompt)
                        capturedImage.value = null
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(
                                text = "Personalized Ingredient Scanner",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Explore Partners") },
                                selected = false,
                                onClick = { scope.launch { drawerState.close() } },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.People,
                                        contentDescription = "Explore Partners"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Subscription Plans") },
                                selected = false,
                                onClick = { scope.launch { drawerState.close() } },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Subscription Plans"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Recipe Guides") },
                                selected = false,
                                onClick = { scope.launch { drawerState.close() } },
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = "Recipe Guides"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Contact Support") },
                                selected = false,
                                onClick = { scope.launch { drawerState.close() } },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "Contact Support"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            // New "Close" button added under Contact Support.
                            NavigationDrawerItem(
                                label = { Text("Close") },
                                selected = false,
                                onClick = { scope.launch { drawerState.close() } },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close Drawer"
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
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
                                    overallScore = if (currentUiState is UiState.Success) currentUiState.overallScore else null,
                                    dietaryPreferences = dietaryPreferences,
                                    cosmeticPreferences = cosmeticPreferences,
                                    onChangePreferences = { currentScreen = "Preferences" },
                                    onShowPreviousScans = { currentScreen = "ScanHistory" },
                                    onBack = { currentScreen = "Home" }
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
            photoUri.let {
                takePictureLauncher.launch(it)
            }
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
