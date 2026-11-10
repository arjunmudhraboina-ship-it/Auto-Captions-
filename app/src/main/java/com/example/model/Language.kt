package com.example.model

enum class Language(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val scriptSample: String,
    val badge: String
) {
    TELUGU(
        code = "te",
        displayName = "Telugu",
        nativeName = "తెలుగు",
        scriptSample = "ఆటో క్యాప్షన్స్ & డైనమిక్ సబ్‌టైటిల్స్",
        badge = "TE"
    ),
    ENGLISH(
        code = "en",
        displayName = "English",
        nativeName = "English",
        scriptSample = "Auto Captions & Dynamic Subtitles",
        badge = "EN"
    ),
    TAMIL(
        code = "ta",
        displayName = "Tamil",
        nativeName = "தமிழ்",
        scriptSample = "தானியங்கி தலைப்புகள் & வசனங்கள்",
        badge = "TA"
    ),
    HINDI(
        code = "hi",
        displayName = "Hindi",
        nativeName = "हिन्दी",
        scriptSample = "ऑटो कैप्शन और वायरल सबटाइटल",
        badge = "HI"
    );

    companion object {
        fun fromCode(code: String): Language =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
    }
}
