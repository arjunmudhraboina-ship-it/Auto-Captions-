package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatio
import com.example.model.BackgroundStyle
import com.example.model.CaptionPosition
import com.example.model.CaptionSegment
import com.example.model.Project
import com.example.model.SubtitleStyle
import com.example.model.WordHighlightAnimation
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoPlayerCanvas(
    project: Project,
    currentTimeMs: Long,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier,
    enableTapToPlay: Boolean = true
) {
    // Find active caption segment at current playback time
    val activeSegment = remember(project.captions, currentTimeMs) {
        project.captions.find { currentTimeMs in it.startTimeMs..it.endTimeMs }
    }

    // Dynamic animated backdrop simulation for video motion
    val infiniteTransition = rememberInfiniteTransition(label = "videoMotion")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveOffset"
    )

    val aspect = project.aspectRatio

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CharcoalBlack)
            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
            .testTag("video_player_canvas"),
        contentAlignment = Alignment.Center
    ) {
        val containerWidth = maxWidth
        val containerHeight = maxHeight

        // Maintain target aspect ratio inside constraints
        Box(
            modifier = Modifier
                .aspectRatio(aspect.ratio, matchHeightConstraintsFirst = true)
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    enabled = enableTapToPlay,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTogglePlay
                )
        ) {
            // Simulated video motion canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Dark cinematic background gradient with subtle glowing aura
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F1019),
                            Color(0xFF141624),
                            Color(0xFF0D0E16)
                        )
                    )
                )

                // Simulated ambient light orb (speaker / subject focus)
                val auraCenterX = w * (0.45f + (waveOffset * 0.1f))
                val auraCenterY = h * (0.35f + (waveOffset * 0.08f))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricPurple.copy(alpha = 0.22f),
                            ElectricBlue.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(auraCenterX, auraCenterY),
                        radius = w * 0.65f
                    ),
                    radius = w * 0.65f,
                    center = Offset(auraCenterX, auraCenterY)
                )

                // Simulated soundwave horizontal bars in background
                val barCount = 18
                val barWidth = w / (barCount * 2f)
                val baseY = h * 0.48f
                for (i in 0 until barCount) {
                    val progress = (i.toFloat() / barCount)
                    val barHeight = ((Math.sin((progress * 3.14 * 2) + (waveOffset * 6.28)) * 0.5 + 0.5) * 36f + 8f).toFloat()
                    val x = (i * barWidth * 2) + barWidth
                    drawRoundRect(
                        color = ElectricPurple.copy(alpha = 0.18f),
                        topLeft = Offset(x, baseY - barHeight / 2),
                        size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                }
            }

            // Aspect Ratio Watermark / Info Badge at top corner
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x88000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) Color(0xFF22C55E) else Color(0xFFF59E0B))
                )
                Text(
                    text = aspect.label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Central Play/Pause Watermark Icon (fades or pulses when paused)
            if (!isPlaying) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA000000))
                        .border(1.5.dp, ElectricPurple.copy(alpha = 0.6f), CircleShape)
                        .testTag("play_pause_overlay_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Dynamic Subtitle Overlay
            if (activeSegment != null) {
                CaptionOverlay(
                    segment = activeSegment,
                    currentTimeMs = currentTimeMs,
                    style = project.style,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptionOverlay(
    segment: CaptionSegment,
    currentTimeMs: Long,
    style: SubtitleStyle,
    modifier: Modifier = Modifier
) {
    val yFraction = when (style.position) {
        CaptionPosition.TOP -> 0.15f
        CaptionPosition.CENTER -> 0.50f
        CaptionPosition.BOTTOM -> style.yOffsetPercent.coerceIn(0.60f, 0.90f)
    }

    BoxWithConstraints(modifier = modifier) {
        val totalHeight = maxHeight
        val yOffsetDp = totalHeight * yFraction

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = yOffsetDp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            val textContent = if (style.isAllCaps) segment.text.uppercase() else segment.text
            val words = segment.words

            // Container decoration based on backgroundStyle
            val containerModifier = when (style.backgroundStyle) {
                BackgroundStyle.NONE -> Modifier
                BackgroundStyle.ROUNDED_BOX -> Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(style.backgroundColor))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                BackgroundStyle.FULL_BAR -> Modifier
                    .fillMaxWidth()
                    .background(Color(style.backgroundColor))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                BackgroundStyle.PILL_TAG -> Modifier
                    .clip(CircleShape)
                    .background(Color(style.backgroundColor))
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            }

            Box(
                modifier = containerModifier,
                contentAlignment = Alignment.Center
            ) {
                if (words.isNotEmpty() && style.isKaraokeEnabled) {
                    // Word-by-word karaoke highlighting
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        words.forEach { wordItem ->
                            val isWordActive = currentTimeMs in wordItem.startMs..wordItem.endMs
                            val isWordPast = currentTimeMs > wordItem.endMs

                            val wordColor = when {
                                isWordActive -> Color(style.highlightWordColor)
                                isWordPast -> Color(style.textColor).copy(alpha = 0.9f)
                                else -> Color(style.textColor).copy(alpha = 0.65f)
                            }

                            val wordScale = if (isWordActive) {
                                when (style.wordHighlightAnimation) {
                                    WordHighlightAnimation.BOUNCE, WordHighlightAnimation.SCALE -> 1.18f
                                    else -> 1.0f
                                }
                            } else 1.0f

                            val displayWord = if (style.isAllCaps) wordItem.word.uppercase() else wordItem.word

                            Text(
                                text = "$displayWord ",
                                style = TextStyle(
                                    fontSize = style.fontSizeSp.sp,
                                    fontWeight = if (isWordActive || style.isBold) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = wordColor,
                                    shadow = Shadow(
                                        color = Color(style.shadowColor),
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    ),
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .scale(wordScale)
                                    .padding(horizontal = 1.dp)
                            )
                        }
                    }
                } else {
                    // Standard plain sentence render
                    Text(
                        text = textContent,
                        style = TextStyle(
                            fontSize = style.fontSizeSp.sp,
                            fontWeight = if (style.isBold) FontWeight.ExtraBold else FontWeight.Normal,
                            color = Color(style.textColor),
                            shadow = Shadow(
                                color = Color(style.shadowColor),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            ),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
