package com.example.model

import java.util.Locale

data class CaptionSegment(
    val id: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val text: String,
    val words: List<WordTimestamp> = emptyList()
) {
    val durationMs: Long
        get() = (endTimeMs - startTimeMs).coerceAtLeast(0)

    fun formattedStart(): String = formatTimestamp(startTimeMs)
    fun formattedEnd(): String = formatTimestamp(endTimeMs)

    companion object {
        fun formatTimestamp(ms: Long): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hundredths = (ms % 1000) / 10
            return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, hundredths)
        }

        fun parseTimestamp(timestamp: String): Long? {
            return try {
                // Accepts mm:ss.xx or mm:ss
                val parts = timestamp.trim().split(":")
                if (parts.size != 2) return null
                val minutes = parts[0].toLong()
                val secParts = parts[1].split(".")
                val seconds = secParts[0].toLong()
                val hundredths = if (secParts.size > 1) secParts[1].padEnd(2, '0').take(2).toLong() else 0L
                (minutes * 60 * 1000) + (seconds * 1000) + (hundredths * 10)
            } catch (e: Exception) {
                null
            }
        }
    }
}
