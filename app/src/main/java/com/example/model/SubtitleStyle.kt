package com.example.model

enum class CaptionPosition(val label: String, val defaultYPercent: Float) {
    TOP("Top", 0.15f),
    CENTER("Center", 0.50f),
    BOTTOM("Bottom", 0.82f)
}

enum class BackgroundStyle(val label: String) {
    NONE("None"),
    ROUNDED_BOX("Box"),
    FULL_BAR("Banner"),
    PILL_TAG("Pill")
}

enum class WordHighlightAnimation(val label: String) {
    COLOR_POP("Color Pop"),
    BOUNCE("Bounce"),
    SCALE("Zoom In"),
    UNDERLINE("Underline")
}

data class SubtitleStyle(
    val id: String = "default_style",
    val presetName: String = "Viral Hormozi",
    val fontName: String = "Poppins",
    val fontSizeSp: Int = 24,
    val isBold: Boolean = true,
    val isAllCaps: Boolean = true,
    val textColor: Long = 0xFFFFFFFF, // ARGB
    val highlightWordColor: Long = 0xFFFACC15, // Hormozi Yellow
    val strokeColor: Long = 0xFF000000,
    val strokeWidthDp: Float = 2.5f,
    val shadowColor: Long = 0x99000000,
    val backgroundStyle: BackgroundStyle = BackgroundStyle.NONE,
    val backgroundColor: Long = 0xCC11111A,
    val position: CaptionPosition = CaptionPosition.BOTTOM,
    val yOffsetPercent: Float = 0.82f,
    val wordHighlightAnimation: WordHighlightAnimation = WordHighlightAnimation.COLOR_POP,
    val maxWordsPerLine: Int = 4,
    val isKaraokeEnabled: Boolean = true
)

data class SubtitlePreset(
    val id: String,
    val name: String,
    val previewSubtitle: String,
    val style: SubtitleStyle,
    val isPro: Boolean = false
) {
    companion object {
        val PRESETS = listOf(
            SubtitlePreset(
                id = "preset_hormozi",
                name = "Hormozi Viral",
                previewSubtitle = "GET 10X MORE VIEWS",
                isPro = false,
                style = SubtitleStyle(
                    presetName = "Hormozi Viral",
                    fontName = "Impact",
                    fontSizeSp = 28,
                    isBold = true,
                    isAllCaps = true,
                    textColor = 0xFFFFFFFF,
                    highlightWordColor = 0xFFFACC15, // Bright Gold
                    strokeColor = 0xFF000000,
                    strokeWidthDp = 3.0f,
                    backgroundStyle = BackgroundStyle.NONE,
                    wordHighlightAnimation = WordHighlightAnimation.BOUNCE
                )
            ),
            SubtitlePreset(
                id = "preset_mrbeast",
                name = "MrBeast Bold",
                previewSubtitle = "WE JUST WON $100,000!",
                isPro = false,
                style = SubtitleStyle(
                    presetName = "MrBeast Bold",
                    fontName = "Montserrat",
                    fontSizeSp = 26,
                    isBold = true,
                    isAllCaps = true,
                    textColor = 0xFFFFFFFF,
                    highlightWordColor = 0xFF22C55E, // Neon Green
                    strokeColor = 0xFF000000,
                    strokeWidthDp = 3.5f,
                    backgroundStyle = BackgroundStyle.PILL_TAG,
                    backgroundColor = 0xAA0B0B14,
                    wordHighlightAnimation = WordHighlightAnimation.SCALE
                )
            ),
            SubtitlePreset(
                id = "preset_cyberpunk",
                name = "Cyberpunk Neon",
                previewSubtitle = "THE FUTURE OF AI VIDEO",
                isPro = true,
                style = SubtitleStyle(
                    presetName = "Cyberpunk Neon",
                    fontName = "Bebas",
                    fontSizeSp = 26,
                    isBold = true,
                    isAllCaps = true,
                    textColor = 0xFF00F5FF, // Cyan
                    highlightWordColor = 0xFFFF007F, // Neon Pink
                    strokeColor = 0xFF050510,
                    strokeWidthDp = 2.0f,
                    backgroundStyle = BackgroundStyle.ROUNDED_BOX,
                    backgroundColor = 0xDD121324,
                    wordHighlightAnimation = WordHighlightAnimation.COLOR_POP
                )
            ),
            SubtitlePreset(
                id = "preset_karaoke_gradient",
                name = "Karaoke Pop",
                previewSubtitle = "Singing Every Single Word",
                isPro = true,
                style = SubtitleStyle(
                    presetName = "Karaoke Pop",
                    fontName = "Poppins",
                    fontSizeSp = 24,
                    isBold = true,
                    isAllCaps = false,
                    textColor = 0xFFF1F5F9,
                    highlightWordColor = 0xFFA855F7, // Electric Violet
                    strokeColor = 0xFF000000,
                    strokeWidthDp = 2.0f,
                    backgroundStyle = BackgroundStyle.FULL_BAR,
                    backgroundColor = 0x99000000,
                    wordHighlightAnimation = WordHighlightAnimation.COLOR_POP
                )
            ),
            SubtitlePreset(
                id = "preset_minimal_clean",
                name = "Minimalist Crisp",
                previewSubtitle = "Clean modern cinema subtitles",
                isPro = false,
                style = SubtitleStyle(
                    presetName = "Minimalist Crisp",
                    fontName = "Inter",
                    fontSizeSp = 20,
                    isBold = false,
                    isAllCaps = false,
                    textColor = 0xFFFFFFFF,
                    highlightWordColor = 0xFFE2E8F0,
                    strokeColor = 0x88000000,
                    strokeWidthDp = 1.0f,
                    backgroundStyle = BackgroundStyle.NONE,
                    wordHighlightAnimation = WordHighlightAnimation.COLOR_POP
                )
            ),
            SubtitlePreset(
                id = "preset_cinema_gold",
                name = "Tollywood Gold",
                previewSubtitle = "సినిమాటిక్ గోల్డెన్ క్యాప్షన్స్",
                isPro = true,
                style = SubtitleStyle(
                    presetName = "Tollywood Gold",
                    fontName = "Poppins",
                    fontSizeSp = 26,
                    isBold = true,
                    isAllCaps = false,
                    textColor = 0xFFFFD700, // Gold
                    highlightWordColor = 0xFFFF5722, // Orange Red
                    strokeColor = 0xFF0B0B14,
                    strokeWidthDp = 2.5f,
                    backgroundStyle = BackgroundStyle.PILL_TAG,
                    backgroundColor = 0xDD1E1E2C,
                    wordHighlightAnimation = WordHighlightAnimation.BOUNCE
                )
            )
        )
    }
}
