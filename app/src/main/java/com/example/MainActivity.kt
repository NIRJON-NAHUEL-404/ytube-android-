package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TubeRed
import com.example.viewmodel.TubeTab
import com.example.viewmodel.TubeViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TubeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TubeLiteApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TubeLiteApp(viewModel: TubeViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val likedVideos by viewModel.likedVideos.collectAsStateWithLifecycle()
    val currentComments by viewModel.currentComments.collectAsStateWithLifecycle()
    val dataSaverSettings by viewModel.dataSaverSettings.collectAsStateWithLifecycle()
    val currentPlayingVideo by viewModel.currentPlayingVideo.collectAsStateWithLifecycle()
    val selectedQuality by viewModel.selectedQuality.collectAsStateWithLifecycle()
    val isPlayerFullScreen by viewModel.isPlayerFullScreen.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Handle Back Press
    BackHandler(enabled = currentPlayingVideo != null) {
        if (isPlayerFullScreen) {
            viewModel.setPlayerFullScreen(false)
        } else {
            viewModel.closePlayer()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Hide bottom bar if video is playing in full screen or active player
            if (currentPlayingVideo == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_navigation_bar")
                ) {
                    // Home
                    NavigationBarItem(
                        selected = selectedTab == TubeTab.HOME,
                        onClick = { viewModel.selectTab(TubeTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == TubeTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("হোম", fontSize = 11.sp, fontWeight = if (selectedTab == TubeTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TubeRed,
                            indicatorColor = TubeRed.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("tab_home")
                    )

                    // Subscriptions
                    NavigationBarItem(
                        selected = selectedTab == TubeTab.SUBSCRIPTIONS,
                        onClick = { viewModel.selectTab(TubeTab.SUBSCRIPTIONS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (subscriptions.isNotEmpty()) {
                                        Badge(containerColor = TubeRed)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == TubeTab.SUBSCRIPTIONS) Icons.Filled.Subscriptions else Icons.Outlined.Subscriptions,
                                    contentDescription = "Subscriptions"
                                )
                            }
                        },
                        label = { Text("সাবস্ক্রিপশন", fontSize = 11.sp, fontWeight = if (selectedTab == TubeTab.SUBSCRIPTIONS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TubeRed,
                            indicatorColor = TubeRed.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("tab_subscriptions")
                    )

                    // Downloads
                    NavigationBarItem(
                        selected = selectedTab == TubeTab.DOWNLOADS,
                        onClick = { viewModel.selectTab(TubeTab.DOWNLOADS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (downloads.isNotEmpty()) {
                                        Badge(containerColor = TubeRed) {
                                            Text("${downloads.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == TubeTab.DOWNLOADS) Icons.Filled.FileDownload else Icons.Outlined.FileDownload,
                                    contentDescription = "Downloads"
                                )
                            }
                        },
                        label = { Text("ডাউনলোড", fontSize = 11.sp, fontWeight = if (selectedTab == TubeTab.DOWNLOADS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TubeRed,
                            indicatorColor = TubeRed.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("tab_downloads")
                    )

                    // Profile / You
                    NavigationBarItem(
                        selected = selectedTab == TubeTab.PROFILE,
                        onClick = { viewModel.selectTab(TubeTab.PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == TubeTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("প্রোফাইল", fontSize = 11.sp, fontWeight = if (selectedTab == TubeTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TubeRed,
                            indicatorColor = TubeRed.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main tabs
            when (selectedTab) {
                TubeTab.HOME -> {
                    HomeScreen(
                        videos = videos,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        dataSaverSettings = dataSaverSettings,
                        onSelectCategory = viewModel::setSelectedCategory,
                        onSearchQueryChange = viewModel::setSearchQuery,
                        onSelectVideo = viewModel::playVideo,
                        onStartDownload = viewModel::startFastDownload,
                        onToggleDataSaver = viewModel::setDataSaverEnabled
                    )
                }

                TubeTab.SUBSCRIPTIONS -> {
                    SubscriptionsScreen(
                        subscriptions = subscriptions,
                        allVideos = videos,
                        onSelectVideo = viewModel::playVideo,
                        onStartDownload = viewModel::startFastDownload,
                        onToggleSubscribe = viewModel::toggleSubscribe
                    )
                }

                TubeTab.DOWNLOADS -> {
                    DownloadsScreen(
                        downloads = downloads,
                        allVideos = videos,
                        onPlayDownloadedVideo = viewModel::playVideo,
                        onDeleteDownload = viewModel::deleteDownload
                    )
                }

                TubeTab.PROFILE -> {
                    ProfileScreen(
                        dataSaverSettings = dataSaverSettings,
                        watchHistory = watchHistory,
                        likedVideos = likedVideos,
                        downloadCount = downloads.size,
                        onSelectVideo = viewModel::playVideo,
                        onToggleDataSaver = viewModel::setDataSaverEnabled,
                        onSetDailyBudget = viewModel::setDailyBudget,
                        onClearHistory = viewModel::clearWatchHistory
                    )
                }
            }

            // Active Video Player Overlay
            if (currentPlayingVideo != null) {
                PlayerScreen(
                    video = currentPlayingVideo!!,
                    allVideos = videos,
                    comments = currentComments,
                    selectedQuality = selectedQuality,
                    isDataSaverEnabled = dataSaverSettings.dataSaverEnabled,
                    isFullScreen = isPlayerFullScreen,
                    onClosePlayer = viewModel::closePlayer,
                    onToggleFullScreen = {
                        viewModel.setPlayerFullScreen(!isPlayerFullScreen)
                    },
                    onSelectQuality = viewModel::setSelectedQuality,
                    onToggleDataSaver = viewModel::setDataSaverEnabled,
                    onToggleLike = viewModel::toggleLike,
                    onToggleDislike = viewModel::toggleDislike,
                    onToggleSubscribe = viewModel::toggleSubscribe,
                    onAddComment = viewModel::addComment,
                    onLikeComment = viewModel::toggleCommentLike,
                    onStartDownload = viewModel::startFastDownload,
                    onSelectVideo = viewModel::playVideo,
                    onPlaybackTick = viewModel::recordPlaybackTick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// Preserve Greeting for test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
