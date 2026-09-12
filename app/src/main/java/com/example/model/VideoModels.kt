package com.example.model

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val channelName: String,
    val channelAvatarUrl: String,
    val subscribersCount: String,
    val viewsCount: String,
    val uploadTime: String,
    val uploadDate: String,
    val duration: String,
    val durationSeconds: Int,
    val category: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false,
    val isDownloaded: Boolean = false,
    val isSubscribed: Boolean = false,
    val tags: List<String> = emptyList()
)

data class Comment(
    val id: String,
    val videoId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val timestamp: String,
    val content: String,
    val likesCount: Int,
    val isLiked: Boolean = false
)

data class NotificationItem(
    val id: String,
    val channelName: String,
    val channelAvatarUrl: String,
    val message: String,
    val timestamp: String,
    val thumbnailUrl: String,
    val isRead: Boolean = false
)

enum class NavigationDestination(val label: String) {
    HOME("Home"),
    SHORTS("Shorts"),
    SUBSCRIPTIONS("Subscriptions"),
    LIBRARY("Library"),
    HISTORY("History"),
    YOUR_VIDEOS("Your Videos"),
    WATCH_LATER("Watch Later"),
    LIKED_VIDEOS("Liked Videos")
}
