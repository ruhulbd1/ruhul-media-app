package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavigationDestination
import com.example.ui.components.YouTubeHeader
import com.example.ui.components.YouTubeSidebar
import com.example.ui.dialogs.AuthDialog
import com.example.ui.dialogs.NotificationsDialog
import com.example.ui.dialogs.ProfileDialog
import com.example.ui.dialogs.VoiceSearchDialog
import com.example.ui.screens.DestinationListScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.YouTubeRed
import com.example.viewmodel.YouTubeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: YouTubeViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                YouTubeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun YouTubeApp(viewModel: YouTubeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredVideos by viewModel.filteredVideos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Handle back button when video player is open or sidebar is open
    BackHandler(enabled = uiState.selectedVideo != null || uiState.isSidebarOpen) {
        when {
            uiState.selectedVideo != null -> viewModel.closeVideoPlayer()
            uiState.isSidebarOpen -> viewModel.setSidebarOpen(false)
        }
    }

    // Show snackbar messages for interactions (e.g. Saved, Subscribed, etc.)
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }

    // Synchronize drawer state with ViewModel
    LaunchedEffect(uiState.isSidebarOpen) {
        if (uiState.isSidebarOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        viewModel.setSidebarOpen(drawerState.isOpen)
    }

    val unreadNotifications = remember(uiState.notifications) {
        uiState.notifications.count { !it.isRead }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            YouTubeSidebar(
                currentDestination = uiState.currentDestination,
                onDestinationSelect = { dest ->
                    viewModel.setDestination(dest)
                    scope.launch { drawerState.close() }
                },
                subscribedChannels = uiState.subscribedChannels,
                modifier = Modifier.testTag("drawer_sidebar")
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (uiState.selectedVideo == null) {
                    YouTubeHeader(
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        onVoiceSearchClick = { viewModel.setVoiceSearchDialogOpen(true) },
                        onNotificationsClick = { viewModel.setNotificationsDialogOpen(true) },
                        onProfileClick = { viewModel.setProfileDialogOpen(true) },
                        onThemeToggleClick = { viewModel.toggleDarkMode() },
                        isDarkMode = uiState.isDarkMode,
                        unreadNotificationsCount = unreadNotifications,
                        currentUser = uiState.currentUser,
                        onSignInClick = { viewModel.setAuthDialogOpen(true) },
                        modifier = Modifier.testTag("youtube_header")
                    )
                }
            },
            bottomBar = {
                // Mobile Bottom Navigation Bar (hidden on wide screens or when playing video)
                if (uiState.selectedVideo == null) {
                    BoxWithConstraints {
                        if (maxWidth < 720.dp) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.background,
                                tonalElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mobile_bottom_nav")
                            ) {
                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavigationDestination.HOME,
                                    onClick = { viewModel.setDestination(NavigationDestination.HOME) },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentDestination == NavigationDestination.HOME) Icons.Default.Home else Icons.Outlined.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Home", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = YouTubeRed,
                                        selectedTextColor = YouTubeRed,
                                        indicatorColor = Color.Transparent
                                    )
                                )

                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavigationDestination.SHORTS,
                                    onClick = { viewModel.setDestination(NavigationDestination.SHORTS) },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentDestination == NavigationDestination.SHORTS) Icons.Default.PlayCircleOutline else Icons.Outlined.PlayCircleOutline,
                                            contentDescription = "Shorts"
                                        )
                                    },
                                    label = { Text("Shorts", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = YouTubeRed,
                                        selectedTextColor = YouTubeRed,
                                        indicatorColor = Color.Transparent
                                    )
                                )

                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavigationDestination.SUBSCRIPTIONS,
                                    onClick = { viewModel.setDestination(NavigationDestination.SUBSCRIPTIONS) },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentDestination == NavigationDestination.SUBSCRIPTIONS) Icons.Default.Subscriptions else Icons.Outlined.Subscriptions,
                                            contentDescription = "Subscriptions"
                                        )
                                    },
                                    label = { Text("Subscriptions", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = YouTubeRed,
                                        selectedTextColor = YouTubeRed,
                                        indicatorColor = Color.Transparent
                                    )
                                )

                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavigationDestination.LIBRARY,
                                    onClick = { viewModel.setDestination(NavigationDestination.LIBRARY) },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentDestination == NavigationDestination.LIBRARY) Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary,
                                            contentDescription = "Library"
                                        )
                                    },
                                    label = { Text("Library", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = YouTubeRed,
                                        selectedTextColor = YouTubeRed,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val isDesktopWidth = maxWidth >= 960.dp

                Row(modifier = Modifier.fillMaxSize()) {
                    // On wide screens (Desktop / Tablet Landscape), show persistent sidebar if not on video player
                    if (isDesktopWidth && uiState.selectedVideo == null) {
                        YouTubeSidebar(
                            currentDestination = uiState.currentDestination,
                            onDestinationSelect = { viewModel.setDestination(it) },
                            subscribedChannels = uiState.subscribedChannels,
                            modifier = Modifier.testTag("desktop_sidebar")
                        )
                    }

                    // Main Content Area
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        val activeVideo = uiState.selectedVideo
                        if (activeVideo != null) {
                            val related = remember(activeVideo.id, uiState.videos) {
                                uiState.videos.filter { it.id != activeVideo.id }
                            }
                            val comments = remember(activeVideo.id, uiState.comments) {
                                uiState.comments[activeVideo.id] ?: emptyList()
                            }

                            VideoPlayerScreen(
                                video = activeVideo,
                                relatedVideos = related,
                                comments = comments,
                                onClose = { viewModel.closeVideoPlayer() },
                                onVideoClick = { viewModel.selectVideo(it) },
                                onLikeClick = { viewModel.toggleLike(activeVideo.id) },
                                onDislikeClick = { viewModel.toggleDislike(activeVideo.id) },
                                onSaveClick = { viewModel.toggleSave(activeVideo.id) },
                                onDownloadClick = { viewModel.toggleDownload(activeVideo.id) },
                                onSubscribeClick = { viewModel.toggleSubscribe(activeVideo.channelName) },
                                onAddComment = { content -> viewModel.addComment(activeVideo.id, content) },
                                onLikeComment = { commentId -> viewModel.likeComment(activeVideo.id, commentId) }
                            )
                        } else {
                            // Feed Content
                            if (uiState.currentDestination == NavigationDestination.HOME) {
                                HomeScreen(
                                    videos = filteredVideos,
                                    selectedCategory = uiState.selectedCategory,
                                    onCategorySelect = { viewModel.setSelectedCategory(it) },
                                    onVideoClick = { viewModel.selectVideo(it) },
                                    onSaveToWatchLater = { viewModel.toggleSave(it.id) },
                                    onShare = { /* Handled inside VideoCard */ }
                                )
                            } else {
                                DestinationListScreen(
                                    destination = uiState.currentDestination,
                                    videos = filteredVideos,
                                    onVideoClick = { viewModel.selectVideo(it) },
                                    onSaveToWatchLater = { viewModel.toggleSave(it.id) },
                                    onShare = { }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Voice Search Dialog
    if (uiState.isVoiceSearchDialogOpen) {
        VoiceSearchDialog(
            onDismiss = { viewModel.setVoiceSearchDialogOpen(false) },
            onSelectQuery = { query -> viewModel.simulateVoiceSearchResult(query) }
        )
    }

    // Notifications Dialog
    if (uiState.isNotificationsDialogOpen) {
        NotificationsDialog(
            notifications = uiState.notifications,
            onDismiss = { viewModel.setNotificationsDialogOpen(false) }
        )
    }

    // User Profile Dialog
    if (uiState.isProfileDialogOpen) {
        ProfileDialog(
            currentUser = uiState.currentUser,
            isDarkMode = uiState.isDarkMode,
            onToggleTheme = { viewModel.toggleDarkMode() },
            onSignInClick = { viewModel.setAuthDialogOpen(true) },
            onSignOutClick = { viewModel.signOut() },
            onDismiss = { viewModel.setProfileDialogOpen(false) }
        )
    }

    // Firebase Authentication Dialog (Login / Sign-up)
    if (uiState.isAuthDialogOpen) {
        AuthDialog(
            isLoading = uiState.isAuthLoading,
            errorMessage = uiState.authErrorMessage,
            onDismiss = { viewModel.setAuthDialogOpen(false) },
            onSignIn = { email, pass -> viewModel.signIn(email, pass) },
            onSignUp = { name, email, pass -> viewModel.signUp(name, email, pass) },
            onResetPassword = { email -> viewModel.resetPassword(email) },
            onDemoLogin = { viewModel.demoLogin() }
        )
    }
}

// Greeting kept for test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
