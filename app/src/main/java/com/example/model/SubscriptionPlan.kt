package com.example.model

enum class BillingCycle(val label: String, val period: String, val savingsBadge: String?) {
    MONTHLY("Monthly", "/ month", null),
    YEARLY("Yearly", "/ year", "SAVE 58%")
}

data class SubscriptionFeature(
    val title: String,
    val freeDesc: String,
    val proDesc: String,
    val isProOnly: Boolean = false
) {
    companion object {
        val ALL_FEATURES = listOf(
            SubscriptionFeature(
                title = "Video Duration",
                freeDesc = "Up to 1 Minute (60s)",
                proDesc = "Up to 60 Minutes",
                isProOnly = false
            ),
            SubscriptionFeature(
                title = "Multilingual Support",
                freeDesc = "Telugu & English",
                proDesc = "Telugu, English, Tamil & Hindi",
                isProOnly = true
            ),
            SubscriptionFeature(
                title = "Smart AI Translation",
                freeDesc = "Not available",
                proDesc = "Translate audio between all 4 languages",
                isProOnly = true
            ),
            SubscriptionFeature(
                title = "Viral Caption Presets",
                freeDesc = "3 Basic Styles",
                proDesc = "All 20+ Trending CapCut & Hormozi Styles",
                isProOnly = true
            ),
            SubscriptionFeature(
                title = "Export Quality",
                freeDesc = "720p with Caption AI watermark",
                proDesc = "1080p & 4K Ultra HD (No Watermark)",
                isProOnly = true
            ),
            SubscriptionFeature(
                title = "SRT & VTT Export",
                freeDesc = "Text only",
                proDesc = "SRT, VTT, Word-level JSON & MP4 Burn",
                isProOnly = true
            ),
            SubscriptionFeature(
                title = "Cloud AI Priority",
                freeDesc = "Standard queue",
                proDesc = "Instant priority dedicated engine",
                isProOnly = true
            )
        )
    }
}

data class UserProfile(
    val id: String = "user_guest",
    val name: String = "Creator",
    val email: String = "creator@captionai.app",
    val isPremium: Boolean = false,
    val isGuest: Boolean = true,
    val preferredLanguage: Language = Language.TELUGU,
    val defaultHighlightKaraoke: Boolean = true
)
