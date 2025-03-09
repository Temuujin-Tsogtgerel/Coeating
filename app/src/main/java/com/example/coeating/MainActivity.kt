package com.example.coeating

import android.Manifest
import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coeating.ui.CoEatingScreen
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

    // State to hold the captured image.
    private var capturedImage = mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register launcher for taking a picture.
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                capturedImage.value = bitmap
            }
        }

        // Register launcher for requesting camera permission.
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                // Handle permission denial if needed.
            }
        }

        setContent {
            CoeatingTheme {
                val bakingViewModel: BakingViewModel = viewModel()
                val uiState by bakingViewModel.uiState.collectAsState()

                // Retrieve saved dietary preferences.
                val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                var savedPreferences by remember { mutableStateOf(prefs.getString("dietary_preferences", "") ?: "") }
                var showPreferencesDialog by remember { mutableStateOf(savedPreferences.isEmpty()) }

                // When an image is captured, build the prompt and send it.
                capturedImage.value?.let { bitmap ->
                    val promptText = if (savedPreferences.isNotBlank()) {
                        "Are these ingredients ${savedPreferences.trim()} friendly?"
                    } else {
                        "Hello, Gemini!"
                    }
                    bakingViewModel.sendPrompt(bitmap, promptText)
                    capturedImage.value = null
                }

                if (showPreferencesDialog) {
                    PreferencesDialog(
                        initialText = savedPreferences,
                        onSave = { input ->
                            savedPreferences = input
                            prefs.edit().putString("dietary_preferences", input).apply()
                            showPreferencesDialog = false
                        }
                    )
                } else {
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
                        dietaryPreferences = savedPreferences
                    )
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
