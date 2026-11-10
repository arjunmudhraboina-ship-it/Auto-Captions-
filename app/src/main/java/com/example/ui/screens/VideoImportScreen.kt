package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoVideoOption
import com.example.data.SampleDataProvider
import com.example.model.CaptionSegment
import com.example.model.Language
import com.example.model.UserProfile
import com.example.ui.components.CustomTopBar
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.HormoziYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoImportScreen(
    userProfile: UserProfile,
    onBack: () -> Unit,
    onVideoSelected: (title: String, uri: String?, durationMs: Long, suggestedLang: Language) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedVideoTitle by remember { mutableStateOf("Tech Gadget Review") }
    var selectedVideoUri by remember { mutableStateOf<String?>(null) }
    var selectedDurationMs by remember { mutableLongStateOf(28_000L) }
    var selectedLang by remember { mutableStateOf(Language.TELUGU) }
    var selectedDemoId by remember { mutableStateOf("demo_1_tech") }

    // Android Photo/Video Picker launcher (zero permission, Google Play compliant)
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri.toString()
            selectedVideoTitle = "Gallery Reel (${uri.lastPathSegment?.takeLast(12) ?: "video.mp4"})"
            selectedDurationMs = 35_000L // Default duration estimation for imported clip
            selectedDemoId = ""
        }
    }

    val maxAllowedMinutes = if (userProfile.isPremium) 60 else 1
    val isDurationExceeded = selectedDurationMs > (maxAllowedMinutes * 60 * 1000L)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBlack)
            .navigationBarsPadding()
            .testTag("video_import_screen")
    ) {
        CustomTopBar(
            title = "Import Video",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gallery Picker Callout Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(1.5.dp, ElectricPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                        .padding(20.dp)
                        .testTag("gallery_picker_trigger_card"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ElectricPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Upload Video",
                                tint = ElectricPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = "Select Video from Gallery",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Supports MP4, MOV, WebM • 9:16 Reels & Shorts",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        OutlinedButton(
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("Browse Device Files", color = ElectricBlue, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Selected Video Details Card
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Current Selection",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ElectricPurple.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = ElectricPurple,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedVideoTitle,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Duration: ${CaptionSegment.formatTimestamp(selectedDurationMs)} • 1080x1920 (9:16)",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Subscription Video Limit Notice
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (userProfile.isPremium) DarkSurface
                            else HormoziYellow.copy(alpha = 0.1f)
                        )
                        .border(
                            1.dp,
                            if (userProfile.isPremium) DarkBorder else HormoziYellow.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (userProfile.isPremium) Icons.Default.WorkspacePremium else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (userProfile.isPremium) HormoziYellow else HormoziYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (userProfile.isPremium) "PRO Plan Active (Up to 60 Minutes)"
                                else "Free Plan: Videos up to 1 Minute (60s)",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (userProfile.isPremium) "You can process long-form YouTube videos, podcasts and tutorials."
                                else "Upgrade to PRO to unlock up to 60 minutes and all 4 languages.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Pre-loaded Demo Clips Section
            item {
                Text(
                    text = "Or Choose a Demo Video to Test Immediately:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(SampleDataProvider.SAMPLE_VIDEOS) { demo ->
                val isSelected = demo.id == selectedDemoId
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                        .border(
                            1.dp,
                            if (isSelected) ElectricPurple else DarkBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            selectedDemoId = demo.id
                            selectedVideoTitle = demo.title
                            selectedDurationMs = demo.durationMs
                            selectedLang = demo.language
                            selectedVideoUri = null
                        }
                        .padding(12.dp)
                        .testTag("demo_video_item_${demo.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElectricPurple else DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = demo.language.badge,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = demo.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${demo.language.displayName} • ${CaptionSegment.formatTimestamp(demo.durationMs)} • ${demo.category}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ElectricPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom CTA: Proceed to Language Selection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkBorder)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    onVideoSelected(selectedVideoTitle, selectedVideoUri, selectedDurationMs, selectedLang)
                },
                enabled = !isDurationExceeded,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("import_proceed_to_language_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Next: Select Language",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
