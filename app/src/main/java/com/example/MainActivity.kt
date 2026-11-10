package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Language
import com.example.ui.components.CustomBottomNav
import com.example.ui.navigation.HomeTab
import com.example.ui.navigation.Screen
import com.example.ui.screens.AiProcessingScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CaptionEditorScreen
import com.example.ui.screens.CaptionStylingScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.VideoImportScreen
import com.example.ui.screens.VideoPreviewScreen
import com.example.ui.theme.CaptionAiTheme
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CaptionAiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.data.ProjectRepository.getInstance(applicationContext)
        enableEdgeToEdge()
        setContent {
            CaptionAiTheme {
                CaptionAiApp()
            }
        }
    }
}

@Composable
fun CaptionAiApp(
    viewModel: CaptionAiViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val homeTab by viewModel.selectedHomeTab.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentTimeMs by viewModel.currentPositionMs.collectAsState()
    val selectedSegmentId by viewModel.selectedSegmentId.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val exportProgress by viewModel.exportProgress.collectAsState()
    val exportCompleteProject by viewModel.exportCompleteProject.collectAsState()
    val dialogMessage by viewModel.dialogMessage.collectAsState()

    // Android System Back Button Handler
    BackHandler(enabled = currentScreen !is Screen.Home && currentScreen !is Screen.Splash) {
        viewModel.navigateBack()
    }

    val isTopLevelTab = currentScreen is Screen.Home

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CharcoalBlack,
        bottomBar = {
            if (isTopLevelTab) {
                CustomBottomNav(
                    selectedTab = homeTab,
                    onTabSelected = { tab ->
                        viewModel.setHomeTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isTopLevelTab) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when (val screen = currentScreen) {
                is Screen.Splash -> {
                    SplashScreen(
                        onContinue = {
                            viewModel.navigateTo(Screen.Auth, clearStack = true)
                        }
                    )
                }

                is Screen.Auth -> {
                    AuthScreen(
                        onLoginSuccess = { name, email ->
                            viewModel.navigateTo(Screen.Home, clearStack = true)
                        },
                        onContinueAsGuest = {
                            viewModel.navigateTo(Screen.Home, clearStack = true)
                        }
                    )
                }

                is Screen.Home -> {
                    when (homeTab) {
                        HomeTab.HOME, HomeTab.PROJECTS -> {
                            HomeScreen(
                                projects = projects,
                                userProfile = userProfile,
                                onNewProject = {
                                    viewModel.navigateTo(Screen.VideoImport)
                                },
                                onAutoCaptions = {
                                    viewModel.navigateTo(Screen.VideoImport)
                                },
                                onOpenProject = { projectId ->
                                    viewModel.selectProject(projectId)
                                    viewModel.navigateTo(Screen.VideoPreview(projectId))
                                },
                                onDuplicateProject = { projectId ->
                                    viewModel.duplicateProject(projectId)
                                },
                                onDeleteProject = { projectId ->
                                    viewModel.deleteProject(projectId)
                                },
                                onOpenSubscription = {
                                    viewModel.navigateTo(Screen.Subscription)
                                },
                                onOpenProfile = {
                                    viewModel.setHomeTab(HomeTab.PROFILE)
                                }
                            )
                        }

                        HomeTab.PREMIUM -> {
                            SubscriptionScreen(
                                userProfile = userProfile,
                                onBack = { viewModel.setHomeTab(HomeTab.HOME) },
                                onPlanAction = { planName ->
                                    viewModel.showBillingNotice(planName)
                                }
                            )
                        }

                        HomeTab.PROFILE -> {
                            ProfileScreen(
                                userProfile = userProfile,
                                onOpenSubscription = {
                                    viewModel.navigateTo(Screen.Subscription)
                                },
                                onLogout = {
                                    viewModel.navigateTo(Screen.Auth, clearStack = true)
                                },
                                onBack = { viewModel.setHomeTab(HomeTab.HOME) }
                            )
                        }
                    }
                }

                is Screen.VideoImport -> {
                    VideoImportScreen(
                        userProfile = userProfile,
                        onBack = { viewModel.navigateBack() },
                        onVideoSelected = { title, uri, durationMs, suggestedLang ->
                            viewModel.startImportFlow(title, uri, durationMs)
                        }
                    )
                }

                is Screen.LanguageSelection -> {
                    LanguageSelectionScreen(
                        videoTitle = screen.videoTitle,
                        userProfile = userProfile,
                        onBack = { viewModel.navigateBack() },
                        onConfirm = { spokenLang, targetLang ->
                            viewModel.confirmLanguageAndProcess(spokenLang, targetLang)
                        },
                        onOpenSubscription = {
                            viewModel.navigateTo(Screen.Subscription)
                        }
                    )
                }

                is Screen.AiProcessing -> {
                    AiProcessingScreen(
                        videoTitle = screen.videoTitle,
                        languageCode = screen.languageCode,
                        onFinished = {
                            viewModel.finalizeAiProcessingAndOpenEditor()
                        },
                        onCancel = {
                            viewModel.navigateBack()
                        }
                    )
                }

                is Screen.VideoPreview -> {
                    val proj = activeProject ?: projects.find { it.id == screen.projectId }
                    if (proj != null) {
                        VideoPreviewScreen(
                            project = proj,
                            currentTimeMs = currentTimeMs,
                            isPlaying = isPlaying,
                            onTogglePlay = { viewModel.togglePlayPause() },
                            onSeek = { pos -> viewModel.seekTo(pos) },
                            onSkipTime = { delta -> viewModel.skipTime(delta) },
                            onSetAspectRatio = { ratio -> viewModel.setAspectRatio(ratio) },
                            onOpenCaptionEditor = { viewModel.navigateTo(Screen.CaptionEditor(proj.id)) },
                            onOpenCaptionStyling = { viewModel.navigateTo(Screen.CaptionStyling(proj.id)) },
                            onOpenExport = { viewModel.navigateTo(Screen.Export(proj.id)) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                is Screen.CaptionEditor -> {
                    val proj = activeProject ?: projects.find { it.id == screen.projectId }
                    if (proj != null) {
                        CaptionEditorScreen(
                            project = proj,
                            currentTimeMs = currentTimeMs,
                            isPlaying = isPlaying,
                            selectedSegmentId = selectedSegmentId,
                            onTogglePlay = { viewModel.togglePlayPause() },
                            onSeek = { pos -> viewModel.seekTo(pos) },
                            onSelectSegment = { id -> viewModel.selectSegment(id) },
                            onUpdateSegmentText = { id, text -> viewModel.updateSegmentText(id, text) },
                            onUpdateSegmentTimestamps = { id, s, e -> viewModel.updateSegmentTimestamps(id, s, e) },
                            onAddSegment = { viewModel.addCaptionSegment() },
                            onSplitSegment = { id, time -> viewModel.splitSegment(id, time) },
                            onDeleteSegment = { id -> viewModel.deleteSegment(id) },
                            onOpenStyling = { viewModel.navigateTo(Screen.CaptionStyling(proj.id)) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                is Screen.CaptionStyling -> {
                    val proj = activeProject ?: projects.find { it.id == screen.projectId }
                    if (proj != null) {
                        CaptionStylingScreen(
                            project = proj,
                            currentTimeMs = currentTimeMs,
                            isPlaying = isPlaying,
                            userProfile = userProfile,
                            onTogglePlay = { viewModel.togglePlayPause() },
                            onApplyPreset = { preset -> viewModel.applyPreset(preset) },
                            onUpdateStyle = { transform -> viewModel.updateStyle(transform) },
                            onOpenSubscription = { viewModel.navigateTo(Screen.Subscription) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                is Screen.Export -> {
                    val proj = activeProject ?: projects.find { it.id == screen.projectId }
                    if (proj != null) {
                        ExportScreen(
                            project = proj,
                            userProfile = userProfile,
                            isExporting = isExporting,
                            exportProgress = exportProgress,
                            completedProject = exportCompleteProject,
                            onStartExport = { format, res -> viewModel.startExport(format, res) },
                            onDismissComplete = { viewModel.dismissExportComplete() },
                            onOpenSubscription = { viewModel.navigateTo(Screen.Subscription) },
                            onBack = { viewModel.navigateBack() },
                            onHome = { viewModel.navigateTo(Screen.Home, clearStack = true) }
                        )
                    }
                }

                is Screen.Subscription -> {
                    SubscriptionScreen(
                        userProfile = userProfile,
                        onBack = { viewModel.navigateBack() },
                        onPlanAction = { planName ->
                            viewModel.showBillingNotice(planName)
                        }
                    )
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        onOpenSubscription = { viewModel.navigateTo(Screen.Subscription) },
                        onLogout = { viewModel.navigateTo(Screen.Auth, clearStack = true) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }

    // Google Play Store Billing Preview Dialog
    if (dialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            containerColor = DarkSurfaceElevated,
            title = {
                Text(
                    text = "Subscription Notice",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = dialogMessage ?: "",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissDialog() }) {
                    Text("Understood", color = ElectricPurple, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
