package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleData
import com.example.firebase.FirebaseAuthService
import com.example.firebase.FirebaseConfig
import com.example.firebase.UserAccount
import com.example.model.Comment
import com.example.model.NavigationDestination
import com.example.model.NotificationItem
import com.example.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class YouTubeUiState(
    val currentDestination: NavigationDestination = NavigationDestination.HOME,
    val selectedVideo: Video? = null,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isDarkMode: Boolean = true,
    val isSidebarOpen: Boolean = false,
    val isVoiceSearchDialogOpen: Boolean = false,
    val isNotificationsDialogOpen: Boolean = false,
    val isProfileDialogOpen: Boolean = false,
    val isAuthDialogOpen: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null,
    val currentUser: UserAccount? = null,
    val isVoiceListening: Boolean = false,
    val recognizedVoiceText: String = "",
    val videos: List<Video> = SampleData.sampleVideos,
    val comments: Map<String, List<Comment>> = SampleData.sampleComments,
    val notifications: List<NotificationItem> = SampleData.sampleNotifications,
    val watchHistory: List<Video> = emptyList(),
    val likedVideos: List<Video> = emptyList(),
    val watchLaterVideos: List<Video> = emptyList(),
    val subscribedChannels: Set<String> = setOf("CodeCraft Academy", "Frontend Prodigy"),
    val snackbarMessage: String? = null
)

class YouTubeViewModel(application: Application) : AndroidViewModel(application) {

    private val authService = FirebaseAuthService(application)

    private val _uiState = MutableStateFlow(
        YouTubeUiState(currentUser = authService.getCurrentUser())
    )
    val uiState: StateFlow<YouTubeUiState> = _uiState.asStateFlow()

    init {
        // Initialize Firebase on ViewModel startup
        FirebaseConfig.initialize(application)
    }

    val filteredVideos: StateFlow<List<Video>> = _uiState.combine(_uiState) { state, _ ->
        val list = when (state.currentDestination) {
            NavigationDestination.HOME -> state.videos
            NavigationDestination.SHORTS -> state.videos.filter { it.durationSeconds < 1800 }
            NavigationDestination.SUBSCRIPTIONS -> state.videos.filter { state.subscribedChannels.contains(it.channelName) }
            NavigationDestination.LIBRARY -> state.videos
            NavigationDestination.HISTORY -> state.watchHistory
            NavigationDestination.YOUR_VIDEOS -> state.videos.take(2)
            NavigationDestination.WATCH_LATER -> state.watchLaterVideos
            NavigationDestination.LIKED_VIDEOS -> state.likedVideos
        }

        val byCategory = if (state.selectedCategory == "All" || state.currentDestination != NavigationDestination.HOME) {
            list
        } else {
            list.filter { it.category.equals(state.selectedCategory, ignoreCase = true) }
        }

        if (state.searchQuery.isBlank()) {
            byCategory
        } else {
            val q = state.searchQuery.trim().lowercase()
            byCategory.filter {
                it.title.lowercase().contains(q) ||
                it.channelName.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) } ||
                it.category.lowercase().contains(q)
            }
        }
    }.combine(_uiState) { filtered, _ -> filtered }
        .let { it as StateFlow<List<Video>> }

    fun setDestination(destination: NavigationDestination) {
        _uiState.update { it.copy(currentDestination = destination, selectedVideo = null) }
    }

    fun selectVideo(video: Video) {
        _uiState.update { current ->
            val updatedHistory = listOf(video) + current.watchHistory.filter { it.id != video.id }
            current.copy(
                selectedVideo = video,
                watchHistory = updatedHistory
            )
        }
    }

    fun closeVideoPlayer() {
        _uiState.update { it.copy(selectedVideo = null) }
    }

    fun setSelectedCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setSidebarOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isSidebarOpen = isOpen) }
    }

    fun setVoiceSearchDialogOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isVoiceSearchDialogOpen = isOpen) }
    }

    fun setNotificationsDialogOpen(isOpen: Boolean) {
        _uiState.update { current ->
            if (isOpen) {
                val markedRead = current.notifications.map { it.copy(isRead = true) }
                current.copy(isNotificationsDialogOpen = true, notifications = markedRead)
            } else {
                current.copy(isNotificationsDialogOpen = false)
            }
        }
    }

    fun setProfileDialogOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isProfileDialogOpen = isOpen) }
    }

    fun setAuthDialogOpen(isOpen: Boolean) {
        _uiState.update {
            it.copy(
                isAuthDialogOpen = isOpen,
                authErrorMessage = if (!isOpen) null else it.authErrorMessage
            )
        }
    }

    // --- Firebase Authentication Functions ---

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val result = authService.signIn(email, pass)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        currentUser = user,
                        isAuthDialogOpen = false,
                        snackbarMessage = "স্বাগতম, ${user.displayName}! সফলভাবে লগইন হয়েছে।"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = error.message ?: "লগইন ব্যর্থ হয়েছে"
                    )
                }
            }
        }
    }

    fun signUp(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val result = authService.signUp(email, pass, name)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        currentUser = user,
                        isAuthDialogOpen = false,
                        snackbarMessage = "অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে! স্বাগতম, ${user.displayName}।"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = error.message ?: "সাইন আপ ব্যর্থ হয়েছে"
                    )
                }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val result = authService.sendPasswordReset(email)
            result.onSuccess { msg ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        isAuthDialogOpen = false,
                        snackbarMessage = msg
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = error.message ?: "পাসওয়ার্ড রিসেট ব্যর্থ হয়েছে"
                    )
                }
            }
        }
    }

    fun demoLogin() {
        val demoUser = UserAccount(
            uid = "demo_ruhul_user_105920612724",
            email = "ruhulamin575@gmail.com",
            displayName = "Ruhul Amin",
            photoUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&q=80",
            idToken = "demo_token"
        )
        authService.saveUserToPrefs(demoUser)
        _uiState.update {
            it.copy(
                currentUser = demoUser,
                isAuthDialogOpen = false,
                snackbarMessage = "টেস্ট অ্যাকাউন্টে প্রবেশ করেছেন: Ruhul Amin"
            )
        }
    }

    fun signOut() {
        authService.signOut()
        _uiState.update {
            it.copy(
                currentUser = null,
                snackbarMessage = "সফলভাবে লগআউট করা হয়েছে।"
            )
        }
    }

    fun simulateVoiceSearchResult(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                isVoiceSearchDialogOpen = false,
                currentDestination = NavigationDestination.HOME,
                selectedVideo = null
            )
        }
    }

    fun toggleLike(videoId: String) {
        _uiState.update { current ->
            val updatedVideos = current.videos.map { v ->
                if (v.id == videoId) {
                    val willLike = !v.isLiked
                    val newLikesCount = if (willLike) v.likesCount + 1 else (v.likesCount - 1).coerceAtLeast(0)
                    v.copy(
                        isLiked = willLike,
                        isDisliked = false,
                        likesCount = newLikesCount
                    )
                } else v
            }
            val targetVideo = updatedVideos.find { it.id == videoId }
            val updatedLiked = if (targetVideo?.isLiked == true) {
                listOf(targetVideo) + current.likedVideos.filter { it.id != videoId }
            } else {
                current.likedVideos.filter { it.id != videoId }
            }
            current.copy(
                videos = updatedVideos,
                selectedVideo = if (current.selectedVideo?.id == videoId) targetVideo else current.selectedVideo,
                likedVideos = updatedLiked
            )
        }
    }

    fun toggleDislike(videoId: String) {
        _uiState.update { current ->
            val updatedVideos = current.videos.map { v ->
                if (v.id == videoId) {
                    val willDislike = !v.isDisliked
                    val newLikesCount = if (v.isLiked) (v.likesCount - 1).coerceAtLeast(0) else v.likesCount
                    v.copy(
                        isDisliked = willDislike,
                        isLiked = false,
                        likesCount = newLikesCount
                    )
                } else v
            }
            val targetVideo = updatedVideos.find { it.id == videoId }
            current.copy(
                videos = updatedVideos,
                selectedVideo = if (current.selectedVideo?.id == videoId) targetVideo else current.selectedVideo,
                likedVideos = current.likedVideos.filter { it.id != videoId }
            )
        }
    }

    fun toggleSave(videoId: String) {
        _uiState.update { current ->
            val updatedVideos = current.videos.map { v ->
                if (v.id == videoId) v.copy(isSaved = !v.isSaved) else v
            }
            val targetVideo = updatedVideos.find { it.id == videoId }
            val willSave = targetVideo?.isSaved == true
            val updatedSaved = if (willSave && targetVideo != null) {
                listOf(targetVideo) + current.watchLaterVideos.filter { it.id != videoId }
            } else {
                current.watchLaterVideos.filter { it.id != videoId }
            }
            val msg = if (willSave) "Saved to Watch Later" else "Removed from Watch Later"
            current.copy(
                videos = updatedVideos,
                selectedVideo = if (current.selectedVideo?.id == videoId) targetVideo else current.selectedVideo,
                watchLaterVideos = updatedSaved,
                snackbarMessage = msg
            )
        }
    }

    fun toggleDownload(videoId: String) {
        _uiState.update { current ->
            val updatedVideos = current.videos.map { v ->
                if (v.id == videoId) v.copy(isDownloaded = !v.isDownloaded) else v
            }
            val targetVideo = updatedVideos.find { it.id == videoId }
            val msg = if (targetVideo?.isDownloaded == true) "Downloaded for offline viewing" else "Download removed"
            current.copy(
                videos = updatedVideos,
                selectedVideo = if (current.selectedVideo?.id == videoId) targetVideo else current.selectedVideo,
                snackbarMessage = msg
            )
        }
    }

    fun toggleSubscribe(channelName: String) {
        _uiState.update { current ->
            val isNowSubscribed = !current.subscribedChannels.contains(channelName)
            val updatedSet = if (isNowSubscribed) {
                current.subscribedChannels + channelName
            } else {
                current.subscribedChannels - channelName
            }
            val updatedVideos = current.videos.map { v ->
                if (v.channelName == channelName) v.copy(isSubscribed = isNowSubscribed) else v
            }
            val targetVideo = current.selectedVideo?.let { sel ->
                if (sel.channelName == channelName) sel.copy(isSubscribed = isNowSubscribed) else sel
            }
            val msg = if (isNowSubscribed) "Subscribed to $channelName" else "Unsubscribed from $channelName"
            current.copy(
                subscribedChannels = updatedSet,
                videos = updatedVideos,
                selectedVideo = targetVideo,
                snackbarMessage = msg
            )
        }
    }

    fun addComment(videoId: String, content: String) {
        if (content.isBlank()) return
        val author = _uiState.value.currentUser?.displayName ?: "You"
        val avatar = _uiState.value.currentUser?.photoUrl?.ifBlank {
            "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80"
        } ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80"

        val newComment = Comment(
            id = "c_${UUID.randomUUID().toString().take(8)}",
            videoId = videoId,
            authorName = author,
            authorAvatarUrl = avatar,
            timestamp = "Just now",
            content = content.trim(),
            likesCount = 0,
            isLiked = false
        )
        _uiState.update { current ->
            val currentList = current.comments[videoId] ?: emptyList()
            val updatedMap = current.comments.toMutableMap().apply {
                put(videoId, listOf(newComment) + currentList)
            }
            current.copy(
                comments = updatedMap,
                snackbarMessage = "Comment posted"
            )
        }
    }

    fun likeComment(videoId: String, commentId: String) {
        _uiState.update { current ->
            val list = current.comments[videoId] ?: emptyList()
            val updatedList = list.map { c ->
                if (c.id == commentId) {
                    val willLike = !c.isLiked
                    val newCount = if (willLike) c.likesCount + 1 else (c.likesCount - 1).coerceAtLeast(0)
                    c.copy(isLiked = willLike, likesCount = newCount)
                } else c
            }
            val updatedMap = current.comments.toMutableMap().apply {
                put(videoId, updatedList)
            }
            current.copy(comments = updatedMap)
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
