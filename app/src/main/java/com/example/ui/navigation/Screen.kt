package com.example.ui.navigation

sealed class Screen {
    data object Splash : Screen()
    data object Auth : Screen()
    data object Home : Screen()
    data object VideoImport : Screen()
    data class LanguageSelection(
        val videoTitle: String,
        val videoUri: String?,
        val durationMs: Long
    ) : Screen()
    data class AiProcessing(
        val videoTitle: String,
        val videoUri: String?,
        val durationMs: Long,
        val languageCode: String,
        val targetLanguageCode: String? = null
    ) : Screen()
    data class VideoPreview(val projectId: String) : Screen()
    data class CaptionEditor(val projectId: String) : Screen()
    data class CaptionStyling(val projectId: String) : Screen()
    data class Export(val projectId: String) : Screen()
    data object Subscription : Screen()
    data object Profile : Screen()
}

enum class HomeTab(val label: String) {
    HOME("Home"),
    PROJECTS("Projects"),
    PREMIUM("Pro"),
    PROFILE("Profile")
}
