package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CaptionSegment
import com.example.model.Project
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CaptionTimeline(
    project: Project,
    currentTimeMs: Long,
    selectedSegmentId: String?,
    onSeek: (Long) -> Unit,
    onSelectSegment: (String) -> Unit,
    onSplitAtCurrent: () -> Unit,
    onAddSegment: () -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    // Base width per second of timeline
    val dpPerSecond = 45.dp * zoomScale
    val totalSeconds = (project.durationMs / 1000f).coerceAtLeast(1f)
    val timelineWidth = dpPerSecond * totalSeconds

    val scrollState = rememberScrollState()

    // Auto-scroll timeline to follow playhead when playing
    LaunchedEffect(currentTimeMs) {
        val currentSec = currentTimeMs / 1000f
        val playheadPx = (currentSec / totalSeconds) * scrollState.maxValue
        if (!scrollState.isScrollInProgress) {
            val targetScroll = (playheadPx - 300).coerceIn(0f, scrollState.maxValue.toFloat()).toInt()
            scrollState.scrollTo(targetScroll)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(12.dp)
            .testTag("caption_timeline")
    ) {
        // Timeline Header: Tools (Split, Add, Zoom)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Timeline",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${CaptionSegment.formatTimestamp(currentTimeMs)} / ${project.formattedDuration()}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Split Action
                IconButton(
                    onClick = onSplitAtCurrent,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .testTag("timeline_split_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallSplit,
                        contentDescription = "Split Segment",
                        tint = ElectricPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Add Segment Action
                IconButton(
                    onClick = onAddSegment,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .testTag("timeline_add_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Segment",
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Zoom controls
                IconButton(
                    onClick = { zoomScale = (zoomScale - 0.25f).coerceAtLeast(0.75f) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { zoomScale = (zoomScale + 0.25f).coerceAtMost(2.5f) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Horizontal Scrollable Waveform & Segments Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CharcoalBlack)
                .horizontalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .width(timelineWidth.coerceAtLeast(400.dp))
                    .fillMaxHeight()
                    .pointerInput(project.durationMs) {
                        detectTapGestures { offset ->
                            val clickedRatio = (offset.x / size.width).coerceIn(0f, 1f)
                            val clickedMs = (clickedRatio * project.durationMs).toLong()
                            onSeek(clickedMs)
                        }
                    }
            ) {
                // Waveform Canvas background
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw time ticks every 2 seconds
                    val tickIntervalSec = 2
                    val numTicks = (totalSeconds / tickIntervalSec).toInt()
                    for (i in 0..numTicks) {
                        val sec = i * tickIntervalSec
                        val x = (sec / totalSeconds) * w
                        drawLine(
                            color = DarkBorder,
                            start = Offset(x, 0f),
                            end = Offset(x, 14f),
                            strokeWidth = 1.5f
                        )
                    }

                    // Draw simulated audio soundwave bars along the center
                    val totalBars = (w / 6f).toInt()
                    val centerY = h * 0.40f
                    for (b in 0 until totalBars) {
                        val x = b * 6f
                        val progress = b.toFloat() / totalBars
                        val heightNoise = (Math.sin(progress * 48.0) * 0.4 + Math.cos(progress * 19.0) * 0.3 + 0.5).coerceIn(0.1, 1.0)
                        val barH = (heightNoise * 26f).toFloat()

                        drawLine(
                            color = Color(0xFF33364E),
                            start = Offset(x, centerY - barH / 2),
                            end = Offset(x, centerY + barH / 2),
                            strokeWidth = 2.5f
                        )
                    }
                }

                // Render Subtitle Segment Blocks along the bottom track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .align(Alignment.BottomStart)
                        .padding(bottom = 6.dp)
                ) {
                    project.captions.forEach { segment ->
                        val startFraction = segment.startTimeMs.toFloat() / project.durationMs.coerceAtLeast(1L)
                        val endFraction = segment.endTimeMs.toFloat() / project.durationMs.coerceAtLeast(1L)
                        val durationFraction = (endFraction - startFraction).coerceAtLeast(0.02f)

                        val isSelected = segment.id == selectedSegmentId
                        val isActive = currentTimeMs in segment.startTimeMs..segment.endTimeMs

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(durationFraction)
                                .offset(x = timelineWidth * startFraction)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> ElectricPurple
                                        isActive -> ElectricBlue.copy(alpha = 0.85f)
                                        else -> DarkSurfaceElevated
                                    }
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else DarkBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectSegment(segment.id)
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = segment.text,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Playhead Scrubber Line
                val playheadFraction = (currentTimeMs.toFloat() / project.durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
                val playheadX = timelineWidth * playheadFraction

                Box(
                    modifier = Modifier
                        .offset(x = playheadX - 1.dp)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.White)
                )

                // Playhead Top Pointer
                Box(
                    modifier = Modifier
                        .offset(x = playheadX - 5.dp, y = 0.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}
