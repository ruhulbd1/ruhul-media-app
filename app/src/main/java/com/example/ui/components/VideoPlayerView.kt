package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.model.Video
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerView(
    video: Video,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember(video.id) { mutableStateOf(true) }
    var currentPositionSeconds by remember(video.id) { mutableIntStateOf(0) }
    val totalDurationSeconds = remember(video.id) { video.durationSeconds.coerceAtLeast(1) }
    var isBuffering by remember(video.id) { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var volume by remember { mutableFloatStateOf(1f) }
    var isMuted by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isSpeedMenuExpanded by remember { mutableStateOf(false) }
    var isSettingsMenuExpanded by remember { mutableStateOf(false) }
    var selectedQuality by remember { mutableStateOf("1080p HD") }

    var mediaPlayer by remember(video.id) { mutableStateOf<MediaPlayer?>(null) }
    var isSurfaceReady by remember(video.id) { mutableStateOf(false) }
    var savedHolder by remember(video.id) { mutableStateOf<SurfaceHolder?>(null) }

    // Auto-hide controls after 4 seconds when playing
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying) {
            delay(4000)
            areControlsVisible = false
        }
    }

    // Playback progress ticker loop
    LaunchedEffect(isPlaying, video.id) {
        while (isPlaying) {
            delay(1000)
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    currentPositionSeconds = (mp.currentPosition / 1000).coerceAtMost(totalDurationSeconds)
                } else {
                    currentPositionSeconds = (currentPositionSeconds + 1).coerceAtMost(totalDurationSeconds)
                }
            } ?: run {
                currentPositionSeconds = (currentPositionSeconds + 1).coerceAtMost(totalDurationSeconds)
            }
        }
    }

    // Initialize/release Android MediaPlayer gracefully
    DisposableEffect(video.id) {
        val mp = MediaPlayer().apply {
            try {
                setDataSource(context, Uri.parse(video.videoUrl))
                isBuffering = true
                setOnPreparedListener {
                    isBuffering = false
                    savedHolder?.let { holder -> setDisplay(holder) }
                    start()
                    isPlaying = true
                }
                setOnCompletionListener {
                    isPlaying = false
                    currentPositionSeconds = totalDurationSeconds
                }
                setOnErrorListener { _, _, _ ->
                    isBuffering = false
                    // Gracefully fallback to simulated playback
                    true
                }
                prepareAsync()
            } catch (e: Exception) {
                isBuffering = false
            }
        }
        mediaPlayer = mp

        onDispose {
            try {
                mp.stop()
                mp.release()
            } catch (_: Exception) {}
        }
    }

    // Sync volume with MediaPlayer
    LaunchedEffect(volume, isMuted, mediaPlayer) {
        val vol = if (isMuted) 0f else volume
        try {
            mediaPlayer?.setVolume(vol, vol)
        } catch (_: Exception) {}
    }

    val formatTime: (Int) -> String = { seconds ->
        val m = seconds / 60
        val s = seconds % 60
        val h = m / 60
        if (h > 0) {
            String.format("%d:%02d:%02d", h, m % 60, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isFullscreen) Modifier.fillMaxSize()
                else Modifier.aspectRatio(16f / 9f)
            )
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                areControlsVisible = !areControlsVisible
            }
            .testTag("video_player_container")
    ) {
        // Video Surface or Fallback Poster
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            savedHolder = holder
                            isSurfaceReady = true
                            try {
                                mediaPlayer?.setDisplay(holder)
                            } catch (_: Exception) {}
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            savedHolder = null
                            isSurfaceReady = false
                            try {
                                mediaPlayer?.setDisplay(null)
                            } catch (_: Exception) {}
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Poster image overlay when buffering
        if (isBuffering || !isSurfaceReady) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Buffering Spinner
        if (isBuffering) {
            CircularProgressIndicator(
                color = YouTubeRed,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        // Overlay Controls (HTML5 Player Style)
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.7f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                // Top Overlay Bar: Title & Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Playback Speed Button
                        Box {
                            IconButton(
                                onClick = { isSpeedMenuExpanded = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Playback Speed",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = isSpeedMenuExpanded,
                                onDismissRequest = { isSpeedMenuExpanded = false }
                            ) {
                                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                    DropdownMenuItem(
                                        text = { Text("${speed}x ${if (speed == 1.0f) "(Normal)" else ""}") },
                                        onClick = {
                                            playbackSpeed = speed
                                            isSpeedMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Quality Settings Menu
                        Box {
                            IconButton(
                                onClick = { isSettingsMenuExpanded = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Video Settings",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = isSettingsMenuExpanded,
                                onDismissRequest = { isSettingsMenuExpanded = false }
                            ) {
                                listOf("1080p60 HD", "720p HD", "480p", "360p", "Auto").forEach { q ->
                                    DropdownMenuItem(
                                        text = { Text(q) },
                                        onClick = {
                                            selectedQuality = q
                                            isSettingsMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Center Play/Pause & Skip Buttons
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Rewind 10s
                    IconButton(
                        onClick = {
                            val newPos = (currentPositionSeconds - 10).coerceAtLeast(0)
                            currentPositionSeconds = newPos
                            mediaPlayer?.seekTo(newPos * 1000)
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Main Play/Pause Button
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable {
                                isPlaying = !isPlaying
                                if (isPlaying) {
                                    try {
                                        mediaPlayer?.start()
                                    } catch (_: Exception) {}
                                } else {
                                    try {
                                        mediaPlayer?.pause()
                                    } catch (_: Exception) {}
                                }
                            }
                            .testTag("play_pause_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Forward 10s
                    IconButton(
                        onClick = {
                            val newPos = (currentPositionSeconds + 10).coerceAtMost(totalDurationSeconds)
                            currentPositionSeconds = newPos
                            mediaPlayer?.seekTo(newPos * 1000)
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Bottom Controls: Seek bar, Time, Volume, Fullscreen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Custom HTML5 Seek Slider
                    Slider(
                        value = currentPositionSeconds.toFloat(),
                        onValueChange = { newSec ->
                            currentPositionSeconds = newSec.toInt()
                        },
                        onValueChangeFinished = {
                            try {
                                mediaPlayer?.seekTo(currentPositionSeconds * 1000)
                            } catch (_: Exception) {}
                        },
                        valueRange = 0f..totalDurationSeconds.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = YouTubeRed,
                            activeTrackColor = YouTubeRed,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .testTag("video_seek_bar")
                    )

                    // Controls Row Below Seek Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Play/Pause mini, Volume, Time
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isPlaying = !isPlaying
                                    if (isPlaying) {
                                        try { mediaPlayer?.start() } catch (_: Exception) {}
                                    } else {
                                        try { mediaPlayer?.pause() } catch (_: Exception) {}
                                    }
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play or Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Volume / Mute
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        isMuted || volume == 0f -> Icons.Default.VolumeMute
                                        volume < 0.5f -> Icons.Default.VolumeDown
                                        else -> Icons.Default.VolumeUp
                                    },
                                    contentDescription = if (isMuted) "Unmute" else "Mute",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Compact Volume slider
                            Slider(
                                value = if (isMuted) 0f else volume,
                                onValueChange = {
                                    volume = it
                                    isMuted = false
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Timestamp (e.g. 02:45 / 14:52)
                            Text(
                                text = "${formatTime(currentPositionSeconds)} / ${formatTime(totalDurationSeconds)}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        // Right: Quality badge & Fullscreen Button
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = selectedQuality,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = onToggleFullscreen,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("fullscreen_button")
                            ) {
                                Icon(
                                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Enter Fullscreen",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
