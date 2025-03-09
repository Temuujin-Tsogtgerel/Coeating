package com.example.coeating

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(bitmap: Bitmap, prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("DEBUG: Sending prompt: $prompt")
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                println("DEBUG: Received response: ${response.text}")
                response.text?.let { outputContent ->
                    // Dummy overall score logic: if the output contains "friendly", mark as pass.
                    val score = outputContent.contains("friendly", ignoreCase = true)
                    _uiState.value = UiState.Success(outputContent, score)
                } ?: run {
                    _uiState.value = UiState.Error("Response text was null")
                }
            } catch (e: Exception) {
                println("DEBUG: Error in API call: ${e.localizedMessage}")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
