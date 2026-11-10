package com.example.model

enum class AspectRatio(val label: String, val ratio: Float, val iconDescription: String) {
    PORTRAIT_9_16("9:16", 9f / 16f, "Reels / TikTok / Shorts"),
    LANDSCAPE_16_9("16:9", 16f / 9f, "YouTube Landscape"),
    SQUARE_1_1("1:1", 1f, "Instagram Square"),
    PORTRAIT_4_5("4:5", 4f / 5f, "Feed Post")
}

data class Project(
    val id: String,
    val title: String,
    val durationMs: Long,
    val videoUri: String? = null,
    val videoThumbnailName: String = "banner_hero_captions",
    val aspectRatio: AspectRatio = AspectRatio.PORTRAIT_9_16,
    val spokenLanguage: Language = Language.TELUGU,
    val translationLanguage: Language? = null,
    val captions: List<CaptionSegment> = emptyList(),
    val style: SubtitleStyle = SubtitleStyle(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val hasWatermark: Boolean = false,
    val isExported: Boolean = false
) {
    val totalWordsCount: Int
        get() = captions.sumOf { it.text.split("\\s+".toRegex()).filter { w -> w.isNotBlank() }.size }

    fun formattedDuration(): String = CaptionSegment.formatTimestamp(durationMs).substringBefore(".")
}

data class ProjectDraft(
    val title: String,
    val videoUri: String?,
    val durationMs: Long,
    val spokenLanguage: Language = Language.TELUGU,
    val translationLanguage: Language? = null,
    val autoPunctuation: Boolean = true,
    val removeFillerWords: Boolean = true,
    val wordLevelTimestamps: Boolean = true
)
