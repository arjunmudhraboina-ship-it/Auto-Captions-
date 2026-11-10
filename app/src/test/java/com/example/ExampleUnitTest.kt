package com.example

import com.example.data.ProjectRepository
import com.example.data.SampleDataProvider
import com.example.model.CaptionSegment
import com.example.model.Language
import com.example.model.ProjectDraft
import com.example.model.SubtitlePreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testLanguageSupport() {
        val supported = Language.entries.map { it.displayName }
        assertTrue(supported.contains("Telugu"))
        assertTrue(supported.contains("English"))
        assertTrue(supported.contains("Tamil"))
        assertTrue(supported.contains("Hindi"))
    }

    @Test
    fun testCaptionTimestampFormatting() {
        val formatted = CaptionSegment.formatTimestamp(65_420L)
        assertEquals("01:05.42", formatted)

        val parsed = CaptionSegment.parseTimestamp("01:05.42")
        assertEquals(65_420L, parsed)
    }

    @Test
    fun testSubtitlePresets() {
        val presets = SubtitlePreset.PRESETS
        assertTrue(presets.isNotEmpty())
        val hormozi = presets.find { it.id == "preset_hormozi" }
        assertNotNull(hormozi)
        assertTrue(hormozi!!.style.isBold)
    }

    @Test
    fun testProjectCreationFromDraft() {
        val repo = ProjectRepository()
        val draft = ProjectDraft(
            title = "Test Telugu Reel",
            videoUri = null,
            durationMs = 20_000L,
            spokenLanguage = Language.TELUGU
        )
        val project = repo.createProjectFromDraft(draft)
        assertEquals("Test Telugu Reel", project.title)
        assertEquals(Language.TELUGU, project.spokenLanguage)
        assertTrue(project.captions.isNotEmpty())
    }
}
