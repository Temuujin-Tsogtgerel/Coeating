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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coeating.ui.CoEatingScreen
import com.example.coeating.ui.CosmeticsScreen
import com.example.coeating.ui.PreferenceSelectionScreen
import com.example.coeating.ui.PreviousScansScreen
import com.example.coeating.ui.theme.CoeatingTheme
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var photoUri: Uri
    private lateinit var photoFile: File

    // Holds the latest captured image.
    private var capturedImage = mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register a launcher for taking a picture.
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                capturedImage.value = bitmap
            }
        }

        // Register a launcher for requesting CAMERA permission.
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                // Permission denied. Handle if needed.
            }
        }

        setContent {
            CoeatingTheme {
                val bakingViewModel: BakingViewModel = viewModel()
                val uiState by bakingViewModel.uiState.collectAsState()

                // Simple string-based navigation for demonstration:
                // "selection" -> PreferenceSelectionScreen
                // "main"      -> CoEatingScreen (scanning flow)
                // "previous"  -> PreviousScansScreen
                // "cosmetics" -> CosmeticsScreen (placeholder)
                var currentScreen by remember { mutableStateOf("selection") }

                // If an image was captured, send it to the BakingViewModel for scanning.
                capturedImage.value?.let { bitmap ->
                    val promptText = "Hello, Gemini!"
                    bakingViewModel.sendPrompt(bitmap, promptText)
                    capturedImage.value = null
                }

                when (currentScreen) {
                    "selection" -> {
                        // The welcome screen with big cards for "Dietary" & "Cosmetics"
                        PreferenceSelectionScreen(
                            userName = "John",  // or load from SharedPreferences
                            onDietaryClick = {
                                // Navigate to scanning flow
                                currentScreen = "main"
                            },
                            onCosmeticsClick = {
                                // Navigate to cosmetics placeholder
                                currentScreen = "cosmetics"
                            }
                        )
                    }
                    "main" -> {
                        // Your existing scanning screen
                        CoEatingScreen(
                            onTakePhotoClick = { checkCameraPermissionAndTakePhoto() },
                            apiResult = when (uiState) {
                                is UiState.Success -> (uiState as UiState.Success).output
                                is UiState.Error -> "Error: " + (uiState as UiState.Error).message
                                is UiState.Loading -> "Loading..."
                                else -> ""
                            },
                            overallScore = when (uiState) {
                                is UiState.Success -> (uiState as UiState.Success).overallScore
                                else -> null
                            },
                            dietaryPreferences = "",  // Or load from user prefs if needed
                            onChangePreferences = {
                                // If you want a dialog or separate logic for changing preferences
                            },
                            onShowPreviousScans = {
                                currentScreen = "previous"
                            }
                        )
                    }
                    "previous" -> {
                        // The list of previous scans
                        PreviousScansScreen(
                            previousScans = bakingViewModel.previousScans,
                            onBack = { currentScreen = "selection" }
                        )
                    }
                    "cosmetics" -> {
                        // Placeholder for cosmetics flow
                        CosmeticsScreen(
                            onBack = { currentScreen = "selection" }
                        )
                    }
                }
            }
        }
    }

    /**
     * Checks if CAMERA permission is granted and either requests permission
     * or starts camera capture.
     */
    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Creates a temporary file and launches the camera intent.
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
