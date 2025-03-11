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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coeating.BakingViewModel
import com.example.coeating.ui.theme.CoEatingScreen
import com.example.coeating.ui.theme.HomeScreen
import com.example.coeating.ui.theme.PreferencesScreen
import com.example.coeating.ui.theme.PreviousScansScreen
import com.example.coeating.ui.theme.CoeatingTheme
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var photoUri: Uri
    private lateinit var photoFile: File

    // Holds the captured image
    private var capturedImage = mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the picture-taking launcher
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    capturedImage.value = bitmap
                }
            }

        // Register permission request
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

                // Track user preferences
                var userName by remember { mutableStateOf("") }
                var dietaryPreferences by remember { mutableStateOf("") }
                var cosmeticPreferences by remember { mutableStateOf("") }

                // Keep track of which screen to show
                var currentScreen by remember { mutableStateOf("Home") }

                // On first run (or whenever userName is blank), force the Preferences screen
                LaunchedEffect(Unit) {
                    if (userName.isBlank()) {
                        currentScreen = "Preferences"
                    }
                }

                // If an image was captured, send it to the model
                LaunchedEffect(capturedImage.value) {
                    capturedImage.value?.let { bitmap ->
                        val prompt = "Check ingredients. If it's food, does it fit $dietaryPreferences? " +
                                "If it's cosmetics, does it fit $cosmeticPreferences?"
                        bakingViewModel.sendPrompt(bitmap, prompt)
                        capturedImage.value = null
                    }
                }

                when (currentScreen) {
                    "Home" -> {
                        HomeScreen(
                            userName = userName,
                            onScanClick = { currentScreen = "Scan" },
                            onPreviousScansClick = { currentScreen = "PreviousScans" },
                            onPreferencesClick = { currentScreen = "Preferences" }
                        )
                    }
                    "Scan" -> {
                        CoEatingScreen(
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
                            onShowPreviousScans = { currentScreen = "PreviousScans" },
                            onBack = { currentScreen = "Home" }
                        )
                    }
                    "PreviousScans" -> {
                        PreviousScansScreen(
                            previousScans = bakingViewModel.previousScans,
                            onBack = { currentScreen = "Home" }
                        )
                    }
                    "Preferences" -> {
                        PreferencesScreen(
                            initialUserName = userName,
                            initialDietaryPreferences = dietaryPreferences,
                            initialCosmeticPreferences = cosmeticPreferences
                        ) { newUserName: String, newDiet: String, newCosmetic: String ->
                            // Save preferences and return to Home screen
                            userName = newUserName
                            dietaryPreferences = newDiet
                            cosmeticPreferences = newCosmetic
                            currentScreen = "Home"
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
