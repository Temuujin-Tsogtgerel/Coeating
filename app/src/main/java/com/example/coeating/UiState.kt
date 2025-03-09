package com.example.coeating

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val output: String, val overallScore: Boolean) : UiState()
    data class Error(val message: String) : UiState()
}
