package com.example.coeating

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class for storing scan results.
data class ScanResult(val foodName: String, val details: String)

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // List of previous scans.
    private val _previousScans = mutableStateListOf<ScanResult>()
    val previousScans: List<ScanResult> get() = _previousScans

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(bitmap: Bitmap, prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    // Dummy overall score: mark as pass if the response contains "friendly".
                    val score = outputContent.contains("friendly", ignoreCase = true)
                    // Extract a food name (using a simple regex).
                    val regex = Regex("image of (?:a|an)?\\s*(\\w+)", RegexOption.IGNORE_CASE)
                    val match = regex.find(outputContent)
                    val foodName = match?.groups?.get(1)?.value ?: "Unnamed Scan"
                    _previousScans.add(ScanResult(foodName, outputContent))
                    _uiState.value = UiState.Success(outputContent, score)
                } ?: run {
                    _uiState.value = UiState.Error("Response text was null")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
