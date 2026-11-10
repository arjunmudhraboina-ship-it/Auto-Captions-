package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CaptionSegment
import com.example.model.Project
import com.example.model.WordTimestamp
import com.example.ui.components.CaptionTimeline
import com.example.ui.components.CustomTopBar
import com.example.ui.components.VideoPlayerCanvas
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptionEditorScreen(
    project: Project,
    currentTimeMs: Long,
    isPlaying: Boolean,
    selectedSegmentId: String?,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onSelectSegment: (String) -> Unit,
    onUpdateSegmentText: (String, String) -> Unit,
    onUpdateSegmentTimestamps: (String, Long, Long) -> Unit,
    onAddSegment: () -> Unit,
    onSplitSegment: (String, Long) -> Unit,
    onDeleteSegment: (String) -> Unit,
    onOpenStyling: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var editingSegmentId by remember { mutableStateOf<String?>(null) }
    var editingText by remember { mutableStateOf("") }
    var editingStartText by remember { mutableStateOf("") }
    var editingEndText by remember { mutableStateOf("") }

    val filteredSegments = remember(project.captions, searchQuery) {
        if (searchQuery.isBlank()) project.captions
        else project.captions.filter { it.text.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBlack)
            .navigationBarsPadding()
            .testTag("caption_editor_screen")
    ) {
        CustomTopBar(
            title = "Caption Timeline Editor",
            onBack = onBack,
            actions = {
                IconButton(
                    onClick = onOpenStyling,
                    modifier = Modifier.testTag("editor_top_style_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatPaint,
                        contentDescription = "Style Presets",
                        tint = ElectricPurple
                    )
                }
            }
        )

        // Compact Mini Video Preview (170dp height)
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

        // Horizontal Waveform & Segment Timeline
        CaptionTimeline(
            project = project,
            currentTimeMs = currentTimeMs,
            selectedSegmentId = selectedSegmentId,
            onSeek = onSeek,
            onSelectSegment = onSelectSegment,
            onSplitAtCurrent = {
                val seg = project.captions.find { currentTimeMs in it.startTimeMs..it.endTimeMs }
                if (seg != null) {
                    onSplitSegment(seg.id, currentTimeMs)
                }
            },
            onAddSegment = onAddSegment,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        // Search in Captions bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search words in captions...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricPurple,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }

        // Editable Segments List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSegments, key = { it.id }) { segment ->
                val isSelected = segment.id == selectedSegmentId
                val isCurrentlyPlaying = currentTimeMs in segment.startTimeMs..segment.endTimeMs
                val isBeingEdited = editingSegmentId == segment.id

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isSelected -> DarkSurfaceElevated
                                isCurrentlyPlaying -> DarkSurfaceElevated.copy(alpha = 0.8f)
                                else -> DarkSurface
                            }
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = when {
                                isSelected -> ElectricPurple
                                isCurrentlyPlaying -> ElectricBlue
                                else -> DarkBorder
                            },
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onSelectSegment(segment.id)
                            onSeek(segment.startTimeMs)
                        }
                        .padding(14.dp)
                        .testTag("caption_segment_${segment.id}")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Top row: timestamps & action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isBeingEdited) {
                                // Editable start & end fields
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = editingStartText,
                                        onValueChange = { editingStartText = it },
                                        label = { Text("Start") },
                                        singleLine = true,
                                        modifier = Modifier.width(90.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ElectricPurple,
                                            unfocusedBorderColor = DarkBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )
                                    Text("-", color = TextSecondary)
                                    OutlinedTextField(
                                        value = editingEndText,
                                        onValueChange = { editingEndText = it },
                                        label = { Text("End") },
                                        singleLine = true,
                                        modifier = Modifier.width(90.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ElectricPurple,
                                            unfocusedBorderColor = DarkBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ElectricPurple.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${segment.formattedStart()} - ${segment.formattedEnd()}",
                                            color = ElectricPurple,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = "${segment.durationMs / 1000f}s",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Actions: Edit / Save, Split, Delete
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isBeingEdited) {
                                    IconButton(
                                        onClick = {
                                            val startParsed = CaptionSegment.parseTimestamp(editingStartText) ?: segment.startTimeMs
                                            val endParsed = CaptionSegment.parseTimestamp(editingEndText) ?: segment.endTimeMs
                                            onUpdateSegmentTimestamps(segment.id, startParsed, endParsed)
                                            onUpdateSegmentText(segment.id, editingText)
                                            editingSegmentId = null
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Save", tint = Color(0xFF22C55E))
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            editingSegmentId = segment.id
                                            editingText = segment.text
                                            editingStartText = segment.formattedStart()
                                            editingEndText = segment.formattedEnd()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text("Edit", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                IconButton(
                                    onClick = { onSplitSegment(segment.id, (segment.startTimeMs + segment.endTimeMs) / 2) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.CallSplit, contentDescription = "Split", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { onDeleteSegment(segment.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Caption Text area
                        if (isBeingEdited) {
                            OutlinedTextField(
                                value = editingText,
                                onValueChange = { editingText = it },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricPurple,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        } else {
                            Text(
                                text = segment.text,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Word-by-word micro chips preview
                        if (segment.words.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                segment.words.forEach { wordItem ->
                                    val isCurrentWord = currentTimeMs in wordItem.startMs..wordItem.endMs
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isCurrentWord) ElectricPurple.copy(alpha = 0.35f)
                                                else DarkSurface
                                            )
                                            .border(
                                                0.5.dp,
                                                if (isCurrentWord) ElectricPurple else DarkBorder,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = wordItem.word,
                                            color = if (isCurrentWord) NeonCyan else TextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = if (isCurrentWord) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
