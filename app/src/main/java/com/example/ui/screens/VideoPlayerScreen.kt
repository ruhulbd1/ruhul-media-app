package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Comment
import com.example.model.Video
import com.example.ui.components.CommentsSection
import com.example.ui.components.RelatedVideosSection
import com.example.ui.components.VideoDetailsSection
import com.example.ui.components.VideoPlayerView

@Composable
fun VideoPlayerScreen(
    video: Video,
    relatedVideos: List<Video>,
    comments: List<Comment>,
    onClose: () -> Unit,
    onVideoClick: (Video) -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    onAddComment: (String) -> Unit,
    onLikeComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFullscreen by remember { mutableStateOf(false) }

    if (isFullscreen) {
        // Fullscreen overlay mode
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("fullscreen_player_container")
        ) {
            VideoPlayerView(
                video = video,
                isFullscreen = true,
                onToggleFullscreen = { isFullscreen = false }
            )
        }
    } else {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("video_player_page")
        ) {
            val isWideLayout = maxWidth >= 840.dp

            if (isWideLayout) {
                // Desktop / Tablet Two-Column Layout (Player + Details + Comments on left, Related Videos on right)
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Column (Player, Details, Comments)
                    Column(
                        modifier = Modifier
                            .weight(0.65f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Top navigation row with back button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.testTag("back_to_feed_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Feed",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        VideoPlayerView(
                            video = video,
                            isFullscreen = false,
                            onToggleFullscreen = { isFullscreen = true }
                        )

                        VideoDetailsSection(
                            video = video,
                            onLikeClick = onLikeClick,
                            onDislikeClick = onDislikeClick,
                            onSaveClick = onSaveClick,
                            onDownloadClick = onDownloadClick,
                            onSubscribeClick = onSubscribeClick
                        )

                        Divider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        CommentsSection(
                            comments = comments,
                            onAddComment = onAddComment,
                            onLikeComment = onLikeComment
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Right Column (Related Videos Panel)
                    Column(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        RelatedVideosSection(
                            relatedVideos = relatedVideos,
                            onVideoClick = onVideoClick
                        )
                    }
                }
            } else {
                // Mobile Single-Column Stacked Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Back Bar on Mobile
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.testTag("back_to_feed_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Feed",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    // Video Player
                    VideoPlayerView(
                        video = video,
                        isFullscreen = false,
                        onToggleFullscreen = { isFullscreen = true }
                    )

                    // Details Section
                    VideoDetailsSection(
                        video = video,
                        onLikeClick = onLikeClick,
                        onDislikeClick = onDislikeClick,
                        onSaveClick = onSaveClick,
                        onDownloadClick = onDownloadClick,
                        onSubscribeClick = onSubscribeClick
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Comments Section
                    CommentsSection(
                        comments = comments,
                        onAddComment = onAddComment,
                        onLikeComment = onLikeComment
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Related Videos below comments on mobile
                    RelatedVideosSection(
                        relatedVideos = relatedVideos,
                        onVideoClick = onVideoClick
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
