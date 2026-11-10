package com.example.data

import com.example.model.BackgroundStyle
import com.example.model.CaptionPosition
import com.example.model.CaptionSegment
import com.example.model.SubtitleStyle
import com.example.model.WordHighlightAnimation
import com.example.model.WordTimestamp
import org.json.JSONArray
import org.json.JSONObject

object ProjectJsonConverter {

    fun captionsToJson(captions: List<CaptionSegment>): String {
        val array = JSONArray()
        for (seg in captions) {
            val segObj = JSONObject().apply {
                put("id", seg.id)
                put("startTimeMs", seg.startTimeMs)
                put("endTimeMs", seg.endTimeMs)
                put("text", seg.text)
                val wordsArr = JSONArray()
                for (w in seg.words) {
                    val wObj = JSONObject().apply {
                        put("id", w.id)
                        put("word", w.word)
                        put("startMs", w.startMs)
                        put("endMs", w.endMs)
                    }
                    wordsArr.put(wObj)
                }
                put("words", wordsArr)
            }
            array.put(segObj)
        }
        return array.toString()
    }

    fun jsonToCaptions(json: String): List<CaptionSegment> {
        if (json.isBlank()) return emptyList()
        val list = mutableListOf<CaptionSegment>()
        return try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val wordsList = mutableListOf<WordTimestamp>()
                if (obj.has("words")) {
                    val wordsArr = obj.getJSONArray("words")
                    for (j in 0 until wordsArr.length()) {
                        val wObj = wordsArr.getJSONObject(j)
                        wordsList.add(
                            WordTimestamp(
                                id = wObj.optString("id", "w_$j"),
                                word = wObj.optString("word", ""),
                                startMs = wObj.optLong("startMs", 0L),
                                endMs = wObj.optLong("endMs", 0L)
                            )
                        )
                    }
                }
                list.add(
                    CaptionSegment(
                        id = obj.optString("id", "seg_$i"),
                        startTimeMs = obj.optLong("startTimeMs", 0L),
                        endTimeMs = obj.optLong("endTimeMs", 0L),
                        text = obj.optString("text", ""),
                        words = wordsList
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun styleToJson(style: SubtitleStyle): String {
        return JSONObject().apply {
            put("id", style.id)
            put("presetName", style.presetName)
            put("fontName", style.fontName)
            put("fontSizeSp", style.fontSizeSp)
            put("isBold", style.isBold)
            put("isAllCaps", style.isAllCaps)
            put("textColor", style.textColor)
            put("highlightWordColor", style.highlightWordColor)
            put("strokeColor", style.strokeColor)
            put("strokeWidthDp", style.strokeWidthDp.toDouble())
            put("backgroundStyle", style.backgroundStyle.name)
            put("backgroundColor", style.backgroundColor)
            put("position", style.position.name)
            put("yOffsetPercent", style.yOffsetPercent.toDouble())
            put("wordHighlightAnimation", style.wordHighlightAnimation.name)
            put("isKaraokeEnabled", style.isKaraokeEnabled)
        }.toString()
    }

    fun jsonToStyle(json: String): SubtitleStyle {
        if (json.isBlank()) return SubtitleStyle()
        return try {
            val obj = JSONObject(json)
            SubtitleStyle(
                id = obj.optString("id", "default_style"),
                presetName = obj.optString("presetName", "Viral Hormozi"),
                fontName = obj.optString("fontName", "Poppins"),
                fontSizeSp = obj.optInt("fontSizeSp", 24),
                isBold = obj.optBoolean("isBold", true),
                isAllCaps = obj.optBoolean("isAllCaps", true),
                textColor = obj.optLong("textColor", 0xFFFFFFFF),
                highlightWordColor = obj.optLong("highlightWordColor", 0xFFFACC15),
                strokeColor = obj.optLong("strokeColor", 0xFF000000),
                strokeWidthDp = obj.optDouble("strokeWidthDp", 2.5).toFloat(),
                backgroundStyle = try {
                    BackgroundStyle.valueOf(obj.optString("backgroundStyle", "NONE"))
                } catch (e: Exception) { BackgroundStyle.NONE },
                backgroundColor = obj.optLong("backgroundColor", 0xCC11111A),
                position = try {
                    CaptionPosition.valueOf(obj.optString("position", "BOTTOM"))
                } catch (e: Exception) { CaptionPosition.BOTTOM },
                yOffsetPercent = obj.optDouble("yOffsetPercent", 0.82).toFloat(),
                wordHighlightAnimation = try {
                    WordHighlightAnimation.valueOf(obj.optString("wordHighlightAnimation", "COLOR_POP"))
                } catch (e: Exception) { WordHighlightAnimation.COLOR_POP },
                isKaraokeEnabled = obj.optBoolean("isKaraokeEnabled", true)
            )
        } catch (e: Exception) {
            SubtitleStyle()
        }
    }
}
