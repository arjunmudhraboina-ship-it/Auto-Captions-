package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Project
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

enum class ExportFormat(val label: String, val ext: String, val icon: ImageVector, val description: String) {
    BURNED_MP4("Burned Subtitles", "MP4", Icons.Default.Movie, "Hardcoded viral video with styled captions"),
    SRT_FILE("SRT Subtitles", "SRT", Icons.Default.Subtitles, "Standard subtitle file for YouTube & Premiere"),
    VTT_FILE("WebVTT Subtitles", "VTT", Icons.Default.Subtitles, "Web-compatible subtitle track"),
    TRANSCRIPT("Plain Text Transcript", "TXT", Icons.Default.Description, "Clean text transcript of spoken words")
}

@Composable
fun ExportScreen(
    project: Project,
    userProfile: UserProfile,
    isExporting: Boolean,
    exportProgress: Float,
    completedProject: Project?,
    onStartExport: (format: String, resolution: String) -> Unit,
    onDismissComplete: () -> Unit,
    onOpenSubscription: () -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedResolution by remember { mutableStateOf("1080p") }
    var selectedFps by remember { mutableStateOf("30 FPS") }
    var selectedFormat by remember { mutableStateOf(ExportFormat.BURNED_MP4) }
    var removeWatermark by remember { mutableStateOf(userProfile.isPremium) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBlack)
            .navigationBarsPadding()
            .testTag("export_screen")
    ) {
        CustomTopBar(
            title = "Export Project",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Project Summary Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = project.title,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aspect: ${project.aspectRatio.label} • Duration: ${project.formattedDuration()} • ${project.spokenLanguage.nativeName}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Preset: ${project.style.presetName} • ${project.captions.size} segments",
                            color = ElectricPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Export Format Selection
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Export Format", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    ExportFormat.entries.forEach { format ->
                        val isSelected = selectedFormat == format
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ElectricPurple else DarkBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedFormat = format }
                                .padding(12.dp)
                                .testTag("export_format_${format.ext.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(format.icon, contentDescription = null, tint = if (isSelected) ElectricPurple else TextSecondary)
                                    Column {
                                        Text(text = format.label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = format.description, color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = ElectricPurple)
                                }
                            }
                        }
                    }
                }
            }

            // Video Resolution
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Resolution Quality", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("720p", "1080p (FHD)", "4K UHD").forEach { res ->
                            val isSelected = selectedResolution.startsWith(res.take(4))
                            val is4K = res.startsWith("4K")
                            val isLocked = is4K && !userProfile.isPremium

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) ElectricPurple else DarkSurface)
                                    .border(1.dp, if (isSelected) ElectricPurple else DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (isLocked) onOpenSubscription()
                                        else selectedResolution = res
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = res,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (is4K) {
                                        Text("PRO", color = HormoziYellow, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Frame Rate
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Frame Rate", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("30 FPS", "60 FPS").forEach { fps ->
                            val isSelected = selectedFps == fps
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) ElectricBlue else DarkSurface)
                                    .border(1.dp, if (isSelected) ElectricBlue else DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedFps = fps }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fps,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Watermark Toggle
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Remove Caption AI Watermark", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (!userProfile.isPremium) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(HormoziYellow.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("PRO", color = HormoziYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = if (userProfile.isPremium) "Watermark disabled for clean creator export"
                                else "Upgrade to PRO to export videos with zero watermark",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = removeWatermark,
                            onCheckedChange = {
                                if (!userProfile.isPremium) onOpenSubscription()
                                else removeWatermark = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricPurple)
                        )
                    }
                }
            }
        }

        // Export CTA Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkBorder)
                .padding(16.dp)
        ) {
            Button(
                onClick = { onStartExport(selectedFormat.ext, selectedResolution) },
                enabled = !isExporting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("export_start_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Export & Render Video", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Export In-Progress Dialog
    if (isExporting) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = DarkSurfaceElevated,
            title = {
                Text("Rendering Video...", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Burning multilingual captions into video frames (${(exportProgress * 100).toInt()}%)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    LinearProgressIndicator(
                        progress = { exportProgress },
                        color = ElectricPurple,
                        trackColor = DarkSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    )
                }
            },
            confirmButton = {}
        )
    }

    // Export Complete Success Dialog
    if (completedProject != null) {
        AlertDialog(
            onDismissRequest = onDismissComplete,
            containerColor = DarkSurfaceElevated,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text(
                    text = "Export Completed!",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Your video with styled ${project.spokenLanguage.displayName} auto captions has been rendered successfully and is ready to share.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, project.title)
                            putExtra(Intent.EXTRA_TEXT, "Created with Caption AI - Multilingual Auto Captions: ${project.title}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Share")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissComplete()
                    onHome()
                }) {
                    Text("My Projects", color = ElectricBlue)
                }
            }
        )
    }
}
