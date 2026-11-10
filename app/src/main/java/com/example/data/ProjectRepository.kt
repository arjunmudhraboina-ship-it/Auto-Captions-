package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.AspectRatio
import com.example.model.CaptionSegment
import com.example.model.Language
import com.example.model.Project
import com.example.model.ProjectDraft
import com.example.model.SubtitlePreset
import com.example.model.SubtitleStyle
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ProjectRepository(
    private val firestore: FirebaseFirestore? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var projectsListener: ListenerRegistration? = null
    private var currentListeningUid: String? = null

    private val _projects = MutableStateFlow<List<Project>>(SampleDataProvider.createInitialProjects())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = "user_default",
            name = "Creator",
            email = "creator@captionai.app",
            isPremium = false,
            isGuest = true
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun currentUserId(): String? {
        return try {
            Firebase.auth.currentUser?.uid
        } catch (t: Throwable) {
            null
        }
    }

    init {
        // Monitor Firebase Auth changes to sync with Firestore
        try {
            val isInitialized = try {
                com.google.firebase.FirebaseApp.getInstance() != null
            } catch (t: Throwable) {
                false
            }
            if (isInitialized) {
                Firebase.auth.addAuthStateListener { auth ->
                    val user = auth.currentUser
                    if (user != null) {
                        _userProfile.update {
                            it.copy(
                                id = user.uid,
                                name = user.displayName ?: "Creator",
                                email = user.email ?: "creator@captionai.app",
                                isGuest = false
                            )
                        }
                        attachFirestoreListeners(user.uid)
                    } else {
                        detachFirestoreListeners()
                    }
                }
            }
        } catch (t: Throwable) {
            // Ignored when running unit tests without Firebase initialized
        }
    }

    private fun attachFirestoreListeners(uid: String) {
        val db = firestore ?: return
        if (currentListeningUid == uid) return
        detachFirestoreListeners()
        currentListeningUid = uid

        val projectsPath = "users/$uid/projects"
        projectsListener = db.collection("users").document(uid).collection("projects")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, projectsPath)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val remoteProjects = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.id
                            val title = doc.getString("title") ?: "Untitled"
                            val durationMs = doc.getLong("durationMs") ?: 20_000L
                            val aspectStr = doc.getString("aspectRatio") ?: "PORTRAIT_9_16"
                            val aspect = try { AspectRatio.valueOf(aspectStr) } catch (e: Exception) { AspectRatio.PORTRAIT_9_16 }
                            val langStr = doc.getString("spokenLanguage") ?: "te"
                            val lang = Language.fromCode(langStr)
                            val transStr = doc.getString("translationLanguage")
                            val transLang = if (transStr != null) Language.fromCode(transStr) else null
                            val captionsJson = doc.getString("captionsJson") ?: "[]"
                            val styleJson = doc.getString("styleJson") ?: "{}"

                            Project(
                                id = id,
                                title = title,
                                durationMs = durationMs,
                                aspectRatio = aspect,
                                spokenLanguage = lang,
                                translationLanguage = transLang,
                                captions = ProjectJsonConverter.jsonToCaptions(captionsJson),
                                style = ProjectJsonConverter.jsonToStyle(styleJson)
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (remoteProjects.isNotEmpty()) {
                        _projects.value = remoteProjects
                    }
                }
            }

        // Sync User Profile document
        val profilePath = "users/$uid"
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val isPrem = doc.getBoolean("isPremium") ?: false
                    _userProfile.update { it.copy(isPremium = isPrem) }
                } else {
                    // Create initial profile doc in Firestore
                    val profileData = mapOf(
                        "userId" to uid,
                        "email" to (_userProfile.value.email),
                        "displayName" to (_userProfile.value.name),
                        "isPremium" to (_userProfile.value.isPremium),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    db.collection("users").document(uid).set(profileData)
                        .addOnFailureListener { e ->
                            handleFirestoreError(e, OperationType.CREATE, profilePath)
                        }
                }
            }
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.GET, profilePath)
            }
    }

    private fun detachFirestoreListeners() {
        projectsListener?.remove()
        projectsListener = null
        currentListeningUid = null
    }

    fun getProject(id: String): Project? {
        return _projects.value.find { it.id == id }
    }

    fun saveProject(project: Project) {
        _projects.update { current ->
            val index = current.indexOfFirst { it.id == project.id }
            if (index >= 0) {
                current.toMutableList().apply { set(index, project.copy(updatedAt = System.currentTimeMillis())) }
            } else {
                listOf(project) + current
            }
        }

        // Persist to Firestore if user is authenticated
        val uid = currentUserId()
        val db = firestore
        if (uid != null && db != null) {
            val docPath = "users/$uid/projects/${project.id}"
            val payload = mapOf(
                "userId" to uid,
                "title" to project.title,
                "durationMs" to project.durationMs,
                "aspectRatio" to project.aspectRatio.name,
                "spokenLanguage" to project.spokenLanguage.code,
                "translationLanguage" to project.translationLanguage?.code,
                "captionsJson" to ProjectJsonConverter.captionsToJson(project.captions),
                "styleJson" to ProjectJsonConverter.styleToJson(project.style),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).collection("projects").document(project.id)
                .set(payload)
                .addOnFailureListener { e ->
                    handleFirestoreError(e, OperationType.WRITE, docPath)
                }
        }
    }

    fun deleteProject(id: String) {
        _projects.update { current -> current.filterNot { it.id == id } }

        val uid = currentUserId()
        val db = firestore
        if (uid != null && db != null) {
            val docPath = "users/$uid/projects/$id"
            db.collection("users").document(uid).collection("projects").document(id)
                .delete()
                .addOnFailureListener { e ->
                    handleFirestoreError(e, OperationType.DELETE, docPath)
                }
        }
    }

    fun duplicateProject(id: String): Project? {
        val original = getProject(id) ?: return null
        val duplicated = original.copy(
            id = "proj_" + UUID.randomUUID().toString().take(8),
            title = "${original.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveProject(duplicated)
        return duplicated
    }

    fun createProjectFromDraft(draft: ProjectDraft): Project {
        val generatedCaptions = SampleDataProvider.generateCaptionsForLanguage(
            language = draft.spokenLanguage,
            durationMs = draft.durationMs
        )
        val defaultPreset = SubtitlePreset.PRESETS.first()
        val newProject = Project(
            id = "proj_" + UUID.randomUUID().toString().take(8),
            title = draft.title.ifBlank { "Untitled Reel" },
            durationMs = draft.durationMs,
            videoUri = draft.videoUri,
            aspectRatio = AspectRatio.PORTRAIT_9_16,
            spokenLanguage = draft.spokenLanguage,
            translationLanguage = draft.translationLanguage,
            captions = generatedCaptions,
            style = defaultPreset.style,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveProject(newProject)
        return newProject
    }

    fun updateCaptions(projectId: String, captions: List<CaptionSegment>) {
        val project = getProject(projectId) ?: return
        saveProject(project.copy(captions = captions))
    }

    fun updateStyle(projectId: String, style: SubtitleStyle) {
        val project = getProject(projectId) ?: return
        saveProject(project.copy(style = style))
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        val uid = currentUserId()
        val db = firestore
        if (uid != null && db != null) {
            val profilePath = "users/$uid"
            val payload = mapOf(
                "userId" to uid,
                "email" to profile.email,
                "displayName" to profile.name,
                "isPremium" to profile.isPremium,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).update(payload)
                .addOnFailureListener { e ->
                    handleFirestoreError(e, OperationType.UPDATE, profilePath)
                }
        }
    }

    fun setPremiumStatus(isPremium: Boolean) {
        _userProfile.update { it.copy(isPremium = isPremium) }
        val uid = currentUserId()
        val db = firestore
        if (uid != null && db != null) {
            val profilePath = "users/$uid"
            db.collection("users").document(uid).update("isPremium", isPremium, "updatedAt", FieldValue.serverTimestamp())
                .addOnFailureListener { e ->
                    handleFirestoreError(e, OperationType.UPDATE, profilePath)
                }
        }
    }

    companion object {
        private var _instance: ProjectRepository? = null

        fun getInstance(context: Context? = null): ProjectRepository {
            if (_instance == null) {
                val db = context?.let { ctx ->
                    try {
                        val dbId = ctx.getString(R.string.firestore_database_id)
                        FirebaseFirestore.getInstance(dbId)
                    } catch (e: Exception) {
                        null
                    }
                }
                _instance = ProjectRepository(db)
            }
            return _instance!!
        }

        val instance: ProjectRepository
            get() = _instance ?: ProjectRepository(null).also { _instance = it }
    }
}
