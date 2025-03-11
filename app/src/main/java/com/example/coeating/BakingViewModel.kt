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

data class ScanResult(val name: String, val details: String)

class BakingViewModel(application: Application) : AndroidViewModel(application) {
    private val scanRepository = ScanRepository(application)

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    private val _previousScans = mutableListOf<ScanResult>()
    val previousScans: List<ScanResult> get() = _previousScans

    // Define the generative model. Ensure that you have added the dependency and that BuildConfig.apiKey is set.
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val scans = scanRepository.getAllScans()
            _previousScans.addAll(scans.map { ScanResult(it.name, it.details) })
        }
    }

    fun sendPrompt(bitmap: Bitmap, prompt: String) {
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
                // Dummy overall score logic.
                val score = outputContent.contains("friendly", ignoreCase = true)

                // Updated regex extraction.
                // This expects the generative model to return the product name in the format:
                // "Product Name: <name>"
                val regex = Regex("Product Type:\\s*(.+)", RegexOption.IGNORE_CASE)
                val match = regex.find(outputContent)
                val foodName = match?.groups?.get(1)?.value?.trim() ?: "Unnamed Scan"

                // Persist the scan result.
                val entity = ScanResultEntity(name = foodName, details = outputContent)
                scanRepository.insertScan(entity)
                _previousScans.add(ScanResult(foodName, outputContent))
                _uiState.value = UiState.Success(outputContent, score)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
