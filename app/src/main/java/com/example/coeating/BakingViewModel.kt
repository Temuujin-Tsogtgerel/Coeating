// File: app/src/main/java/com/example/coeating/BakingViewModel.kt
package com.example.coeating

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coeating.data.ScanRepository
import com.example.coeating.data.ScanResultEntity
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ScanResult(
    val id: Int,
    val name: String,
    val details: String,
    val imagePath: String? = null
)

class BakingViewModel(application: Application) : AndroidViewModel(application) {
    private val scanRepository = ScanRepository(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    private val _previousScans = mutableListOf<ScanResult>()
    val previousScans: List<ScanResult> get() = _previousScans

    // Define the generative model.
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val scans = scanRepository.getAllScans()
            _previousScans.addAll(scans.map { ScanResult(it.id, it.name, it.details, it.imagePath) })
        }
    }

    fun deleteScan(scan: ScanResult) {
        viewModelScope.launch(Dispatchers.IO) {
            scanRepository.deleteScan(ScanResultEntity(scan.id, scan.name, scan.details, scan.imagePath))
            _previousScans.remove(scan)
        }
    }

    fun sendPrompt(bitmap: Bitmap, prompt: String, imagePath: String? = null) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contentPayload = content {
                    image(bitmap)
                    text(prompt)
                }
                val response = generativeModel.generateContent(contentPayload)
                val outputContent = response.text ?: run {
                    _uiState.value = UiState.Error("Response text was null")
                    return@launch
                }
                // Use the full trimmed output instead of a regex extraction.
                val fullResult = outputContent.trim()

                // Dummy overall score logic.
                val score = fullResult.contains("friendly", ignoreCase = true)

                // For scan history, we use a shortened version as the 'name'
                val displayName = if (fullResult.length > 30) fullResult.substring(0, 30) + "..." else fullResult

                // Persist the scan result with full details.
                val entity = ScanResultEntity(name = displayName, details = fullResult, imagePath = imagePath)
                val insertedId = scanRepository.insertScan(entity).toInt()
                _previousScans.add(ScanResult(insertedId, displayName, fullResult, imagePath))
                _uiState.value = UiState.Success(fullResult, score)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
