package com.example.data

import com.example.model.AspectRatio
import com.example.model.CaptionPosition
import com.example.model.CaptionSegment
import com.example.model.Language
import com.example.model.Project
import com.example.model.SubtitlePreset
import com.example.model.SubtitleStyle
import com.example.model.WordTimestamp

object SampleDataProvider {

    val SAMPLE_VIDEOS = listOf(
        DemoVideoOption(
            id = "demo_1_tech",
            title = "Tech Gadget Review",
            durationMs = 28_000L,
            language = Language.TELUGU,
            category = "Reel / Shorts",
            resolution = "1080 x 1920 (9:16)",
            thumbnailDescription = "Modern tech gadgets review video"
        ),
        DemoVideoOption(
            id = "demo_2_viral",
            title = "Viral Content Secrets",
            durationMs = 24_000L,
            language = Language.ENGLISH,
            category = "Podcast Clip",
            resolution = "1080 x 1920 (9:16)",
            thumbnailDescription = "Creator speaking about video algorithms"
        ),
        DemoVideoOption(
            id = "demo_3_tamil",
            title = "Tamil Tech Vlog",
            durationMs = 26_000L,
            language = Language.TAMIL,
            category = "Vlog Highlight",
            resolution = "1080 x 1920 (9:16)",
            thumbnailDescription = "Tamil tech review and AI future"
        ),
        DemoVideoOption(
            id = "demo_4_hindi",
            title = "Hindi AI Tips & Tricks",
            durationMs = 30_000L,
            language = Language.HINDI,
            category = "Tutorial Short",
            resolution = "1080 x 1920 (9:16)",
            thumbnailDescription = "Hindi mobile content creation tutorial"
        )
    )

    fun createInitialProjects(): List<Project> {
        val teluguProject = Project(
            id = "proj_telugu_01",
            title = "AI టెక్ రివ్యూ 2026 - టాప్ గాడ్జెట్స్",
            durationMs = 24_000L,
            aspectRatio = AspectRatio.PORTRAIT_9_16,
            spokenLanguage = Language.TELUGU,
            style = SubtitlePreset.PRESETS.first { it.id == "preset_hormozi" }.style.copy(
                fontSizeSp = 26,
                position = CaptionPosition.BOTTOM,
                yOffsetPercent = 0.80f
            ),
            captions = listOf(
                CaptionSegment(
                    id = "seg_te_1",
                    startTimeMs = 0L,
                    endTimeMs = 4_000L,
                    text = "ఈ రోజు మనం చూడబోతున్నాం",
                    words = listOf(
                        WordTimestamp("w1", "ఈ", 0L, 800L),
                        WordTimestamp("w2", "రోజు", 800L, 1600L),
                        WordTimestamp("w3", "మనం", 1600L, 2600L),
                        WordTimestamp("w4", "చూడబోతున్నాం", 2600L, 4000L)
                    )
                ),
                CaptionSegment(
                    id = "seg_te_2",
                    startTimeMs = 4_200L,
                    endTimeMs = 9_000L,
                    text = "అత్యంత శక్తివంతమైన AI టూల్స్",
                    words = listOf(
                        WordTimestamp("w5", "అత్యంత", 4200L, 5400L),
                        WordTimestamp("w6", "శక్తివంతమైన", 5400L, 7000L),
                        WordTimestamp("w7", "AI", 7000L, 8000L),
                        WordTimestamp("w8", "టూల్స్", 8000L, 9000L)
                    )
                ),
                CaptionSegment(
                    id = "seg_te_3",
                    startTimeMs = 9_400L,
                    endTimeMs = 15_000L,
                    text = "ఇది మీ వీడియోలకు ఆటో క్యాప్షన్స్ ఇస్తుంది",
                    words = listOf(
                        WordTimestamp("w9", "ఇది", 9400L, 10400L),
                        WordTimestamp("w10", "మీ", 10400L, 11400L),
                        WordTimestamp("w11", "వీడియోలకు", 11400L, 12800L),
                        WordTimestamp("w12", "ఆటో", 12800L, 13800L),
                        WordTimestamp("w13", "క్యాప్షన్స్", 13800L, 14600L),
                        WordTimestamp("w14", "ఇస్తుంది", 14600L, 15000L)
                    )
                ),
                CaptionSegment(
                    id = "seg_te_4",
                    startTimeMs = 15_400L,
                    endTimeMs = 24_000L,
                    text = "కేవలం ఒక్క క్లిక్ తో పూర్తి వీడియో సిద్ధం!",
                    words = listOf(
                        WordTimestamp("w15", "కేవలం", 15400L, 17000L),
                        WordTimestamp("w16", "ఒక్క", 17000L, 18500L),
                        WordTimestamp("w17", "క్లిక్", 18500L, 20000L),
                        WordTimestamp("w18", "తో", 20000L, 21000L),
                        WordTimestamp("w19", "పూర్తి", 21000L, 22200L),
                        WordTimestamp("w20", "సిద్ధం!", 22200L, 24000L)
                    )
                )
            )
        )

        val englishProject = Project(
            id = "proj_english_02",
            title = "CapCut Style Viral Captions",
            durationMs = 22_000L,
            aspectRatio = AspectRatio.PORTRAIT_9_16,
            spokenLanguage = Language.ENGLISH,
            style = SubtitlePreset.PRESETS.first { it.id == "preset_mrbeast" }.style.copy(
                fontSizeSp = 28,
                position = CaptionPosition.BOTTOM,
                yOffsetPercent = 0.78f
            ),
            captions = listOf(
                CaptionSegment(
                    id = "seg_en_1",
                    startTimeMs = 0L,
                    endTimeMs = 3_800L,
                    text = "If you want 10x more views",
                    words = listOf(
                        WordTimestamp("ew1", "If", 0L, 500L),
                        WordTimestamp("ew2", "you", 500L, 1100L),
                        WordTimestamp("ew3", "want", 1100L, 1800L),
                        WordTimestamp("ew4", "10x", 1800L, 2600L),
                        WordTimestamp("ew5", "more", 2600L, 3100L),
                        WordTimestamp("ew6", "views", 3100L, 3800L)
                    )
                ),
                CaptionSegment(
                    id = "seg_en_2",
                    startTimeMs = 4_000L,
                    endTimeMs = 9_500L,
                    text = "You NEED animated karaoke captions",
                    words = listOf(
                        WordTimestamp("ew7", "You", 4000L, 4600L),
                        WordTimestamp("ew8", "NEED", 4600L, 5600L),
                        WordTimestamp("ew9", "animated", 5600L, 6800L),
                        WordTimestamp("ew10", "karaoke", 6800L, 8100L),
                        WordTimestamp("ew11", "captions", 8100L, 9500L)
                    )
                ),
                CaptionSegment(
                    id = "seg_en_3",
                    startTimeMs = 10_000L,
                    endTimeMs = 16_000L,
                    text = "Notice how each word lights up right now",
                    words = listOf(
                        WordTimestamp("ew12", "Notice", 10000L, 11000L),
                        WordTimestamp("ew13", "how", 11000L, 11600L),
                        WordTimestamp("ew14", "each", 11600L, 12400L),
                        WordTimestamp("ew15", "word", 12400L, 13200L),
                        WordTimestamp("ew16", "lights", 13200L, 14100L),
                        WordTimestamp("ew17", "up", 14100L, 14800L),
                        WordTimestamp("ew18", "right", 14800L, 15400L),
                        WordTimestamp("ew19", "now", 15400L, 16000L)
                    )
                ),
                CaptionSegment(
                    id = "seg_en_4",
                    startTimeMs = 16_500L,
                    endTimeMs = 22_000L,
                    text = "Stop editing manually and use Caption AI!",
                    words = listOf(
                        WordTimestamp("ew20", "Stop", 16500L, 17400L),
                        WordTimestamp("ew21", "editing", 17400L, 18500L),
                        WordTimestamp("ew22", "manually", 18500L, 19800L),
                        WordTimestamp("ew23", "use", 19800L, 20600L),
                        WordTimestamp("ew24", "Caption", 20600L, 21400L),
                        WordTimestamp("ew25", "AI!", 21400L, 22000L)
                    )
                )
            )
        )

        val tamilProject = Project(
            id = "proj_tamil_03",
            title = "தமிழ் AI டெக் ரீல்ஸ் 2026",
            durationMs = 20_000L,
            aspectRatio = AspectRatio.PORTRAIT_9_16,
            spokenLanguage = Language.TAMIL,
            style = SubtitlePreset.PRESETS.first { it.id == "preset_cinema_gold" }.style.copy(
                fontSizeSp = 25,
                position = CaptionPosition.BOTTOM,
                yOffsetPercent = 0.82f
            ),
            captions = listOf(
                CaptionSegment(
                    id = "seg_ta_1",
                    startTimeMs = 0L,
                    endTimeMs = 5_000L,
                    text = "வணக்கம் நண்பர்களே!",
                    words = listOf(
                        WordTimestamp("tw1", "வணக்கம்", 0L, 2200L),
                        WordTimestamp("tw2", "நண்பர்களே!", 2200L, 5000L)
                    )
                ),
                CaptionSegment(
                    id = "seg_ta_2",
                    startTimeMs = 5_400L,
                    endTimeMs = 12_000L,
                    text = "உங்கள் வீடியோக்களுக்கு தானியங்கி வசனங்கள் உருவாக்கலாம்",
                    words = listOf(
                        WordTimestamp("tw3", "உங்கள்", 5400L, 6800L),
                        WordTimestamp("tw4", "வீடியோக்களுக்கு", 6800L, 8500L),
                        WordTimestamp("tw5", "தானியங்கி", 8500L, 10200L),
                        WordTimestamp("tw6", "வசனங்கள்", 10200L, 12000L)
                    )
                ),
                CaptionSegment(
                    id = "seg_ta_3",
                    startTimeMs = 12_500L,
                    endTimeMs = 20_000L,
                    text = "மிகவும் துல்லியமான தமிழ் உச்சரிப்பு!",
                    words = listOf(
                        WordTimestamp("tw7", "மிகவும்", 12500L, 14500L),
                        WordTimestamp("tw8", "துல்லியமான", 14500L, 16800L),
                        WordTimestamp("tw9", "தமிழ்", 16800L, 18200L),
                        WordTimestamp("tw10", "உச்சரிப்பு!", 18200L, 20000L)
                    )
                )
            )
        )

        val hindiProject = Project(
            id = "proj_hindi_04",
            title = "AI वीडियो एडिटिंग सीक्रेट्स",
            durationMs = 26_000L,
            aspectRatio = AspectRatio.PORTRAIT_9_16,
            spokenLanguage = Language.HINDI,
            style = SubtitlePreset.PRESETS.first { it.id == "preset_cyberpunk" }.style.copy(
                fontSizeSp = 24,
                position = CaptionPosition.BOTTOM,
                yOffsetPercent = 0.80f
            ),
            captions = listOf(
                CaptionSegment(
                    id = "seg_hi_1",
                    startTimeMs = 0L,
                    endTimeMs = 4_500L,
                    text = "क्या आप भी रील्स बनाते हैं?",
                    words = listOf(
                        WordTimestamp("hw1", "क्या", 0L, 800L),
                        WordTimestamp("hw2", "आप", 800L, 1500L),
                        WordTimestamp("hw3", "भी", 1500L, 2200L),
                        WordTimestamp("hw4", "रील्स", 2200L, 3300L),
                        WordTimestamp("hw5", "बनाते", 3300L, 4000L),
                        WordTimestamp("hw6", "हैं?", 4000L, 4500L)
                    )
                ),
                CaptionSegment(
                    id = "seg_hi_2",
                    startTimeMs = 5_000L,
                    endTimeMs = 13_000L,
                    text = "तो ये नया AI टूल आपके बहुत काम आएगा",
                    words = listOf(
                        WordTimestamp("hw7", "तो", 5000L, 5800L),
                        WordTimestamp("hw8", "ये", 5800L, 6600L),
                        WordTimestamp("hw9", "नया", 6600L, 7800L),
                        WordTimestamp("hw10", "AI", 7800L, 8800L),
                        WordTimestamp("hw11", "टूल", 8800L, 9900L),
                        WordTimestamp("hw12", "आपके", 9900L, 11200L),
                        WordTimestamp("hw13", "काम", 11200L, 12200L),
                        WordTimestamp("hw14", "आएगा", 12200L, 13000L)
                    )
                ),
                CaptionSegment(
                    id = "seg_hi_3",
                    startTimeMs = 13_500L,
                    endTimeMs = 26_000L,
                    text = "अब कैप्शन टाइप करने की झंझट खत्म!",
                    words = listOf(
                        WordTimestamp("hw15", "अब", 13500L, 15000L),
                        WordTimestamp("hw16", "कैप्शन", 15000L, 17500L),
                        WordTimestamp("hw17", "टाइप", 17500L, 19800L),
                        WordTimestamp("hw18", "करने", 19800L, 21800L),
                        WordTimestamp("hw19", "की", 21800L, 23000L),
                        WordTimestamp("hw20", "झंझट", 23000L, 24500L),
                        WordTimestamp("hw21", "खत्म!", 24500L, 26000L)
                    )
                )
            )
        )

        return listOf(teluguProject, englishProject, tamilProject, hindiProject)
    }

    fun generateCaptionsForLanguage(language: Language, durationMs: Long): List<CaptionSegment> {
        val segmentDuration = 4_000L
        val count = (durationMs / segmentDuration).toInt().coerceAtLeast(3)
        val textSnippets = when (language) {
            Language.TELUGU -> listOf(
                "ఈ వీడియోలో అద్భుతమైన విషయాలు చూద్దాం",
                "ఆటో క్యాప్షన్స్ ఉపయోగించడం చాలా సులభం",
                "ప్రతి ఒక్క పదానికి ఖచ్చితమైన సమయం",
                "మీ రీల్స్ ఇప్పుడు మరింత వైరల్ అవుతాయి",
                "క్యాప్షన్ AI తో క్రియేటివిటీ రెట్టింపు",
                "తెలుగు సబ్‌టైటిల్స్ ఎప్పుడూ ఇంత సులువుగా రాలేదు"
            )
            Language.ENGLISH -> listOf(
                "Welcome to the easiest caption editor",
                "Word by word captions increase viewer retention",
                "Syncing audio waveforms with precision AI",
                "Customize colors, fonts and animations in seconds",
                "Export directly to your camera roll in 4K",
                "Boost your reel engagement effortlessly"
            )
            Language.TAMIL -> listOf(
                "வணக்கம்! இந்த வீடியோவில் முக்கிய குறிப்புகள்",
                "தானியங்கி வசனங்கள் மிக எளிதாக உருவாக்கலாம்",
                "ஒவ்வொரு வார்த்தைக்கும் துல்லியமான அசைவுகள்",
                "உங்கள் ரீல்ஸ் பார்வையாளர்களை ஈர்க்கும்",
                "புதிய AI அம்சங்கள் இப்போது கிடைக்கின்றன"
            )
            Language.HINDI -> listOf(
                "इस नए वीडियो में आपका स्वागत है",
                "ऑटो कैप्शन से आपकी रील्स वायरल होंगी",
                "हर शब्द पर सही टाइमिंग और स्टाइल",
                "अब मैन्युअल टाइपिंग करने की ज़रूरत नहीं",
                "कैप्शन AI के साथ अपनी वीडियो को चमकाएं"
            )
        }

        val segments = mutableListOf<CaptionSegment>()
        for (i in 0 until count) {
            val startMs = i * segmentDuration
            val endMs = (startMs + segmentDuration - 400L).coerceAtMost(durationMs)
            val snippet = textSnippets[i % textSnippets.size]
            val wordsList = snippet.split(" ").filter { it.isNotBlank() }
            val wordDur = if (wordsList.isNotEmpty()) (endMs - startMs) / wordsList.size else 0L

            val words = wordsList.mapIndexed { wIdx, word ->
                WordTimestamp(
                    id = "gen_${i}_${wIdx}",
                    word = word,
                    startMs = startMs + (wIdx * wordDur),
                    endMs = startMs + ((wIdx + 1) * wordDur)
                )
            }

            segments.add(
                CaptionSegment(
                    id = "gen_seg_$i",
                    startTimeMs = startMs,
                    endTimeMs = endMs,
                    text = snippet,
                    words = words
                )
            )
        }
        return segments
    }
}

data class DemoVideoOption(
    val id: String,
    val title: String,
    val durationMs: Long,
    val language: Language,
    val category: String,
    val resolution: String,
    val thumbnailDescription: String
)
