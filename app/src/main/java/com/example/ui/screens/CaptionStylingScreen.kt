package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundStyle
import com.example.model.CaptionPosition
import com.example.model.Project
import com.example.model.SubtitlePreset
import com.example.model.SubtitleStyle
import com.example.model.UserProfile
import com.example.model.WordHighlightAnimation
import com.example.ui.components.CustomTopBar
import com.example.ui.components.VideoPlayerCanvas
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.HormoziYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMint
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ViralOrange

enum class StylingTab(val label: String) {
    PRESETS("Presets"),
    FONT("Font & Size"),
    COLORS("Colors"),
    BACKGROUND("Background"),
    POSITION("Position & FX")
}

@Composable
fun CaptionStylingScreen(
    project: Project,
    currentTimeMs: Long,
    isPlaying: Boolean,
    userProfile: UserProfile,
    onTogglePlay: () -> Unit,
    onApplyPreset: (SubtitlePreset) -> Unit,
    onUpdateStyle: ((SubtitleStyle) -> SubtitleStyle) -> Unit,
    onOpenSubscription: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(StylingTab.PRESETS) }
    val style = project.style

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBlack)
            .navigationBarsPadding()
            .testTag("caption_styling_screen")
    ) {
        CustomTopBar(
            title = "Caption Styling & Effects",
            onBack = onBack
        )

        // Live Video Player Preview (showing changes in real-time)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            VideoPlayerCanvas(
                project = project,
                currentTimeMs = currentTimeMs,
                isPlaying = isPlaying,
                onTogglePlay = onTogglePlay,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Horizontal Category Tab Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(StylingTab.entries.toList()) { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ElectricPurple else DarkSurface)
                        .border(1.dp, if (isSelected) ElectricPurple else DarkBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("style_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Selected Tab Content Pane
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                StylingTab.PRESETS -> {
                    item {
                        Text(
                            text = "Viral CapCut Presets",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "One-tap viral subtitle styles used by top creators",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    items(SubtitlePreset.PRESETS) { preset ->
                        val isCurrent = style.presetName == preset.name
                        val isLocked = preset.isPro && !userProfile.isPremium

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isCurrent) DarkSurfaceElevated else DarkSurface)
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) ElectricPurple else DarkBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (isLocked) onOpenSubscription()
                                    else onApplyPreset(preset)
                                }
                                .padding(16.dp)
                                .testTag("preset_card_${preset.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = preset.name,
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (preset.isPro) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(HormoziYellow.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "PRO",
                                                    color = HormoziYellow,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    // Preview Subtitle rendering
                                    Text(
                                        text = preset.previewSubtitle,
                                        color = Color(preset.style.highlightWordColor),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(ElectricPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                } else if (isLocked) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = HormoziYellow)
                                }
                            }
                        }
                    }
                }

                StylingTab.FONT -> {
                    // Font Family choices
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Font Family", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            val fonts = listOf("Poppins", "Montserrat", "Impact", "Bebas", "Inter", "System")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                fonts.take(3).forEach { f ->
                                    val isSelected = style.fontName == f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ElectricPurple else DarkSurface)
                                            .border(1.dp, if (isSelected) ElectricPurple else DarkBorder, RoundedCornerShape(10.dp))
                                            .clickable { onUpdateStyle { it.copy(fontName = f) } }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = f, color = if (isSelected) Color.White else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                fonts.drop(3).forEach { f ->
                                    val isSelected = style.fontName == f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ElectricPurple else DarkSurface)
                                            .border(1.dp, if (isSelected) ElectricPurple else DarkBorder, RoundedCornerShape(10.dp))
                                            .clickable { onUpdateStyle { it.copy(fontName = f) } }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = f, color = if (isSelected) Color.White else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Font Size Slider
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Font Size", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${style.fontSizeSp} sp", color = ElectricPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = style.fontSizeSp.toFloat(),
                                onValueChange = { onUpdateStyle { s -> s.copy(fontSizeSp = it.toInt()) } },
                                valueRange = 16f..40f,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricPurple,
                                    activeTrackColor = ElectricPurple,
                                    inactiveTrackColor = DarkSurfaceElevated
                                )
                            )
                        }
                    }

                    // Toggles: Bold & All-Caps
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Bold Weight", color = TextPrimary, fontSize = 14.sp)
                                Switch(
                                    checked = style.isBold,
                                    onCheckedChange = { onUpdateStyle { s -> s.copy(isBold = it) } },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricPurple)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ALL CAPS TEXT", color = TextPrimary, fontSize = 14.sp)
                                Switch(
                                    checked = style.isAllCaps,
                                    onCheckedChange = { onUpdateStyle { s -> s.copy(isAllCaps = it) } },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricPurple)
                                )
                            }
                        }
                    }
                }

                StylingTab.COLORS -> {
                    // Active Word Karaoke Highlight Color Palette
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Active Word Highlight Color (Karaoke Pop)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            val highlightColors = listOf(
                                HormoziYellow to 0xFFFACC15,
                                NeonMint to 0xFF10B981,
                                NeonCyan to 0xFF06B6D4,
                                ElectricPurple to 0xFFA855F7,
                                NeonPink to 0xFFEC4899,
                                ViralOrange to 0xFFFB923C,
                                Color.White to 0xFFFFFFFF
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                highlightColors.forEach { (c, argb) ->
                                    val isSelected = style.highlightWordColor == argb
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(c)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) Color.White else DarkBorder,
                                                shape = CircleShape
                                            )
                                            .clickable { onUpdateStyle { it.copy(highlightWordColor = argb) } },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = CharcoalBlack, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Base Text Color
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Base Text Color", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            val baseColors = listOf(
                                Color.White to 0xFFFFFFFF,
                                Color(0xFFE2E8F0) to 0xFFE2E8F0,
                                Color(0xFFFEF08A) to 0xFFFEF08A,
                                Color(0xFFBAE6FD) to 0xFFBAE6FD,
                                Color(0xFF000000) to 0xFF000000
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                baseColors.forEach { (c, argb) ->
                                    val isSelected = style.textColor == argb
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(c)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) ElectricPurple else DarkBorder,
                                                shape = CircleShape
                                            )
                                            .clickable { onUpdateStyle { it.copy(textColor = argb) } }
                                    )
                                }
                            }
                        }
                    }
                }

                StylingTab.BACKGROUND -> {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Background Decoration Style", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BackgroundStyle.entries.forEach { bgStyle ->
                                    val isSelected = style.backgroundStyle == bgStyle
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ElectricPurple else DarkSurface)
                                            .border(1.dp, if (isSelected) ElectricPurple else DarkBorder, RoundedCornerShape(10.dp))
                                            .clickable { onUpdateStyle { it.copy(backgroundStyle = bgStyle) } }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = bgStyle.label,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                StylingTab.POSITION -> {
                    // Vertical Position selector (Top, Center, Bottom)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Vertical Screen Position", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CaptionPosition.entries.forEach { pos ->
                                    val isSelected = style.position == pos
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ElectricPurple else DarkSurface)
                                            .border(1.dp, if (isSelected) ElectricPurple else DarkBorder, RoundedCornerShape(10.dp))
                                            .clickable {
                                                onUpdateStyle {
                                                    it.copy(position = pos, yOffsetPercent = pos.defaultYPercent)
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = pos.label, color = if (isSelected) Color.White else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Y-Offset Fine Tuner Slider
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Vertical Alignment Offset", color = TextPrimary, fontSize = 13.sp)
                                Text("${(style.yOffsetPercent * 100).toInt()}%", color = ElectricPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = style.yOffsetPercent,
                                onValueChange = { onUpdateStyle { s -> s.copy(yOffsetPercent = it) } },
                                valueRange = 0.10f..0.90f,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricPurple,
                                    activeTrackColor = ElectricPurple,
                                    inactiveTrackColor = DarkSurfaceElevated
                                )
                            )
                        }
                    }

                    // Karaoke Highlighting Animation Style
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Active Word Animation", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WordHighlightAnimation.entries.forEach { anim ->
                                    val isSelected = style.wordHighlightAnimation == anim
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ElectricBlue else DarkSurface)
                                            .border(1.dp, if (isSelected) ElectricBlue else DarkBorder, RoundedCornerShape(10.dp))
                                            .clickable { onUpdateStyle { it.copy(wordHighlightAnimation = anim) } }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = anim.label, color = if (isSelected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Karaoke Highlighting master toggle
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface)
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Word-by-Word Karaoke", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Highlight each word dynamically as it is spoken", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = style.isKaraokeEnabled,
                                onCheckedChange = { onUpdateStyle { s -> s.copy(isKaraokeEnabled = it) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricPurple)
                            )
                        }
                    }
                }
            }
        }
    }
}
