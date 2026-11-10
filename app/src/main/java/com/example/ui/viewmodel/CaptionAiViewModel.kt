package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProjectRepository
import com.example.model.AspectRatio
import com.example.model.CaptionPosition
import com.example.model.CaptionSegment
import com.example.model.Language
import com.example.model.Project
import com.example.model.ProjectDraft
import com.example.model.SubtitlePreset
import com.example.model.SubtitleStyle
import com.example.model.UserProfile
import com.example.model.WordTimestamp
import com.example.ui.navigation.HomeTab
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class CaptionAiViewModel(
    private val repository: ProjectRepository = ProjectRepository.instance
) : ViewModel() {

    constructor() : this(ProjectRepository.instance)

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Splash).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                this@apply.value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    private val _selectedHomeTab = MutableStateFlow(HomeTab.HOME)
    val selectedHomeTab: StateFlow<HomeTab> = _selectedHomeTab.asStateFlow()

    // Projects & Profile from Repository
    val projects: StateFlow<List<Project>> = repository.projects
    val userProfile: StateFlow<UserProfile> = repository.userProfile

    // Active Project in Editor/Preview
    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    // Playback Controller State
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private var playbackJob: Job? = null

    // Video Import Draft State
    private val _currentDraft = MutableStateFlow<ProjectDraft?>(null)
    val currentDraft: StateFlow<ProjectDraft?> = _currentDraft.asStateFlow()

    // Search query in captions
    private val _captionSearchQuery = MutableStateFlow("")
    val captionSearchQuery: StateFlow<String> = _captionSearchQuery.asStateFlow()

    // Selected segment in caption editor
    private val _selectedSegmentId = MutableStateFlow<String?>(null)
    val selectedSegmentId: StateFlow<String?> = _selectedSegmentId.asStateFlow()

    // Export State
    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportProgress = MutableStateFlow(0f)
    val exportProgress: StateFlow<Float> = _exportProgress.asStateFlow()

    private val _exportCompleteProject = MutableStateFlow<Project?>(null)
    val exportCompleteProject: StateFlow<Project?> = _exportCompleteProject.asStateFlow()

    // Notice dialog (e.g. for Play Store billing preview message)
    private val _dialogMessage = MutableStateFlow<String?>(null)
    val dialogMessage: StateFlow<String?> = _dialogMessage.asStateFlow()

    init {
        // Initialize active project with first project if available
        viewModelScope.launch {
            repository.projects.collect { list ->
                if (_activeProject.value == null && list.isNotEmpty()) {
                    _activeProject.value = list.first()
                }
            }
        }
    }

    // Navigation methods
    fun navigateTo(screen: Screen, clearStack: Boolean = false) {
        pausePlayback()
        if (clearStack) {
            _screenStack.value = listOf(screen)
        } else {
            _screenStack.update { it + screen }
        }
    }

    fun navigateBack(): Boolean {
        pausePlayback()
        val current = _screenStack.value
        return if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    fun setHomeTab(tab: HomeTab) {
        _selectedHomeTab.value = tab
    }

    fun selectProject(projectId: String) {
        val proj = repository.getProject(projectId)
        _activeProject.value = proj
        _currentPositionMs.value = 0L
        _selectedSegmentId.value = proj?.captions?.firstOrNull()?.id
        pausePlayback()
    }

    // Playback control
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        val project = _activeProject.value ?: return
        if (project.durationMs <= 0) return

        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 50L
            while (isActive && _isPlaying.value) {
                delay(stepMs)
                val next = _currentPositionMs.value + stepMs
                if (next >= project.durationMs) {
                    _currentPositionMs.value = 0L // Loop video
                } else {
                    _currentPositionMs.value = next
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }

    fun seekTo(positionMs: Long) {
        val project = _activeProject.value ?: return
        val clamped = positionMs.coerceIn(0L, project.durationMs)
        _currentPositionMs.value = clamped
    }

    fun skipTime(deltaMs: Long) {
        val next = _currentPositionMs.value + deltaMs
        seekTo(next)
    }

    // Caption Editing
    fun selectSegment(segmentId: String) {
        _selectedSegmentId.value = segmentId
        val seg = _activeProject.value?.captions?.find { it.id == segmentId }
        if (seg != null) {
            seekTo(seg.startTimeMs)
        }
    }

    fun updateSegmentText(segmentId: String, newText: String) {
        val proj = _activeProject.value ?: return
        val updated = proj.captions.map { seg ->
            if (seg.id == segmentId) {
                // Re-align words across the segment duration
                val wordsList = newText.split("\\s+".toRegex()).filter { it.isNotBlank() }
                val wordDur = if (wordsList.isNotEmpty()) seg.durationMs / wordsList.size else 0L
                val newWords = wordsList.mapIndexed { i, w ->
                    WordTimestamp(
                        id = "${seg.id}_w_$i",
                        word = w,
                        startMs = seg.startTimeMs + (i * wordDur),
                        endMs = seg.startTimeMs + ((i + 1) * wordDur)
                    )
                }
                seg.copy(text = newText, words = newWords)
            } else seg
        }
        val newProj = proj.copy(captions = updated)
        _activeProject.value = newProj
        repository.saveProject(newProj)
    }

    fun updateSegmentTimestamps(segmentId: String, startMs: Long, endMs: Long) {
        val proj = _activeProject.value ?: return
        if (endMs <= startMs) return
        val updated = proj.captions.map { seg ->
            if (seg.id == segmentId) {
                val wordsList = seg.words
                val wordDur = if (wordsList.isNotEmpty()) (endMs - startMs) / wordsList.size else 0L
                val newWords = wordsList.mapIndexed { i, w ->
                    w.copy(startMs = startMs + (i * wordDur), endMs = startMs + ((i + 1) * wordDur))
                }
                seg.copy(startTimeMs = startMs, endTimeMs = endMs, words = newWords)
            } else seg
        }.sortedBy { it.startTimeMs }
        val newProj = proj.copy(captions = updated)
        _activeProject.value = newProj
        repository.saveProject(newProj)
    }

    fun addCaptionSegment(atPositionMs: Long? = null) {
        val proj = _activeProject.value ?: return
        val targetStart = atPositionMs ?: _currentPositionMs.value
        val targetEnd = (targetStart + 3_000L).coerceAtMost(proj.durationMs)
        val newSeg = CaptionSegment(
            id = "seg_" + UUID.randomUUID().toString().take(6),
            startTimeMs = targetStart,
            endTimeMs = targetEnd,
            text = "New caption segment",
            words = listOf(
                WordTimestamp("nw1", "New", targetStart, targetStart + 900L),
                WordTimestamp("nw2", "caption", targetStart + 900L, targetStart + 1800L),
                WordTimestamp("nw3", "segment", targetStart + 1800L, targetEnd)
            )
        )
        val updated = (proj.captions + newSeg).sortedBy { it.startTimeMs }
        val newProj = proj.copy(captions = updated)
        _activeProject.value = newProj
        _selectedSegmentId.value = newSeg.id
        repository.saveProject(newProj)
    }

    fun deleteSegment(segmentId: String) {
        val proj = _activeProject.value ?: return
        val updated = proj.captions.filterNot { it.id == segmentId }
        val newProj = proj.copy(captions = updated)
        _activeProject.value = newProj
        _selectedSegmentId.value = updated.firstOrNull()?.id
        repository.saveProject(newProj)
    }

    fun splitSegment(segmentId: String, atTimeMs: Long) {
        val proj = _activeProject.value ?: return
        val seg = proj.captions.find { it.id == segmentId } ?: return
        if (atTimeMs <= seg.startTimeMs + 400L || atTimeMs >= seg.endTimeMs - 400L) return

        val words = seg.text.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val mid = (words.size / 2).coerceAtLeast(1)
        val text1 = words.take(mid).joinToString(" ")
        val text2 = words.drop(mid).joinToString(" ").ifBlank { "..." }

        val seg1 = seg.copy(endTimeMs = atTimeMs, text = text1)
        val seg2 = CaptionSegment(
            id = "seg_" + UUID.randomUUID().toString().take(6),
            startTimeMs = atTimeMs,
            endTimeMs = seg.endTimeMs,
            text = text2
        )

        val updated = (proj.captions.filterNot { it.id == segmentId } + seg1 + seg2).sortedBy { it.startTimeMs }
        val newProj = proj.copy(captions = updated)
        _activeProject.value = newProj
        _selectedSegmentId.value = seg2.id
        repository.saveProject(newProj)
    }

    // Styling Methods
    fun applyPreset(preset: SubtitlePreset) {
        val proj = _activeProject.value ?: return
        val updatedStyle = preset.style.copy(
            id = preset.id,
            presetName = preset.name,
            position = proj.style.position,
            yOffsetPercent = proj.style.yOffsetPercent
        )
        val newProj = proj.copy(style = updatedStyle)
        _activeProject.value = newProj
        repository.saveProject(newProj)
    }

    fun updateStyle(transform: (SubtitleStyle) -> SubtitleStyle) {
        val proj = _activeProject.value ?: return
        val newStyle = transform(proj.style)
        val newProj = proj.copy(style = newStyle)
        _activeProject.value = newProj
        repository.saveProject(newProj)
    }

    fun setAspectRatio(ratio: AspectRatio) {
        val proj = _activeProject.value ?: return
        val newProj = proj.copy(aspectRatio = ratio)
        _activeProject.value = newProj
        repository.saveProject(newProj)
    }

    // Draft & AI Processing Flow
    fun startImportFlow(videoTitle: String, videoUri: String?, durationMs: Long) {
        _currentDraft.value = ProjectDraft(
            title = videoTitle,
            videoUri = videoUri,
            durationMs = durationMs
        )
        navigateTo(
            Screen.LanguageSelection(
                videoTitle = videoTitle,
                videoUri = videoUri,
                durationMs = durationMs
            )
        )
    }

    fun confirmLanguageAndProcess(spokenLanguage: Language, targetLanguage: Language?) {
        val draft = _currentDraft.value ?: return
        _currentDraft.value = draft.copy(
            spokenLanguage = spokenLanguage,
            translationLanguage = targetLanguage
        )
        navigateTo(
            Screen.AiProcessing(
                videoTitle = draft.title,
                videoUri = draft.videoUri,
                durationMs = draft.durationMs,
                languageCode = spokenLanguage.code,
                targetLanguageCode = targetLanguage?.code
            )
        )
    }

    fun finalizeAiProcessingAndOpenEditor() {
        val draft = _currentDraft.value ?: return
        val newProject = repository.createProjectFromDraft(draft)
        _activeProject.value = newProject
        _currentDraft.value = null
        _currentPositionMs.value = 0L
        navigateTo(Screen.VideoPreview(newProject.id), clearStack = true)
    }

    // Export simulation
    fun startExport(format: String, resolution: String) {
        val proj = _activeProject.value ?: return
        _isExporting.value = true
        _exportProgress.value = 0f
        viewModelScope.launch {
            for (i in 1..100) {
                delay(30)
                _exportProgress.value = i / 100f
            }
            _isExporting.value = false
            _exportCompleteProject.value = proj
        }
    }

    fun dismissExportComplete() {
        _exportCompleteProject.value = null
    }

    // Subscription & Billing Notice
    fun showBillingNotice(planName: String) {
        _dialogMessage.value = "Google Play Billing: Preview Mode\n\nPlan selected: $planName.\nIn production, this connects securely via Google Play Billing. No payment has been processed."
    }

    fun dismissDialog() {
        _dialogMessage.value = null
    }

    fun deleteProject(projectId: String) {
        repository.deleteProject(projectId)
        if (_activeProject.value?.id == projectId) {
            _activeProject.value = repository.projects.value.firstOrNull()
        }
    }

    fun duplicateProject(projectId: String) {
        repository.duplicateProject(projectId)
    }

    fun setCaptionSearchQuery(query: String) {
        _captionSearchQuery.value = query
    }

    override fun onCleared() {
        super.onCleared()
        pausePlayback()
    }
}
