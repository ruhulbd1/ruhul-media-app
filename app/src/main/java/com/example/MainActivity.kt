package com.aistudio.youtube.cvyqmp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ইউটিউব স্টাইলের লেআউট ও ভিডিও তালিকা
        val listView = ListView(this)
        val videoList = arrayOf(
            "▶ ট্রেন্ডিং মিউজিক ভিডিও ২০২৬",
            "▶ অ্যান্ড্রয়েড অ্যাপ ডেভেলপমেন্ট টিউটোরিয়াল",
            "▶ এআই দিয়ে কীভাবে ভিডিও বানাবেন",
            "▶ টেকনোলজি নিউজ এবং আপডেট",
            "▶ লাইভ স্ট্রিমিং ও ব্লগিং গাইড"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, videoList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, "প্লে হচ্ছে: ${videoList[position]}", Toast.LENGTH_SHORT).show()
        }
        
        setContentView(listView)
    }
}
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
