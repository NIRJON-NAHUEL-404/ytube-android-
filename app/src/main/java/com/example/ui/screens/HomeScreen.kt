package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DataSaverSettingsEntity
import com.example.model.Video
import com.example.model.VideoQuality
import com.example.ui.components.FastDownloadDialog
import com.example.ui.components.GridVideoCard
import com.example.ui.components.VideoCard

private val TabActiveYellow = Color(0xFFE5A93C)
private val TabInactiveGray = Color(0xFF9E9E9E)
private val DarkBg = Color(0xFF0F0F0F)
private val SurfaceDark = Color(0xFF181818)

@Composable
fun HomeScreen(
    videos: List<Video>,
    selectedCategory: String,
    searchQuery: String,
    searchSuggestions: List<String> = emptyList(),
    dataSaverSettings: DataSaverSettingsEntity,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectVideo: (Video) -> Unit,
    onStartDownload: (Video, VideoQuality) -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var videoToDownload by remember { mutableStateOf<Video?>(null) }
    val focusManager = LocalFocusManager.current

    // 4 Top Tabs shown in screenshot: Search, YouTube, Music, More
    val topTabs = listOf("Search", "YouTube", "Music", "More")
    val currentTab = when {
        selectedCategory == "Search" || searchQuery.isNotBlank() -> "Search"
        selectedCategory == "Music" -> "Music"
        selectedCategory == "More" -> "More"
        else -> "YouTube"
    }

    if (videoToDownload != null) {
        FastDownloadDialog(
            video = videoToDownload!!,
            onStartDownload = { q -> onStartDownload(videoToDownload!!, q) },
            onDismiss = { videoToDownload = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // TOP TAB HEADER (Search | YouTube | Music | More) - Matching Screenshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            topTabs.forEach { tabName ->
                val isSelected = currentTab == tabName
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            if (tabName == "Search") {
                                onSelectCategory("Search")
                            } else {
                                onSearchQueryChange("")
                                onSelectCategory(tabName)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("top_tab_$tabName")
                ) {
                    Text(
                        text = tabName,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TabInactiveGray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Amber/Yellow Underline for Active Tab (as shown in screenshot)
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(3.dp)
                            .background(
                                color = if (isSelected) TabActiveYellow else Color.Transparent,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        // SEARCH BAR (when Search tab is active or search query present)
        AnimatedVisibility(
            visible = currentTab == "Search",
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "ইউটিউবের অফিশিয়াল সার্ভারে খুঁজুন...",
                            fontSize = 14.sp,
                            color = TabInactiveGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TabActiveYellow,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TabActiveYellow,
                        unfocusedBorderColor = Color(0xFF2C2C2C),
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Quick Popular Search Tags
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickTags = listOf(
                        "Kamariya slowed reverb",
                        "Mix Music of Asia",
                        "দুটো মনে লেগে গেছে জোড়া",
                        "Kiya Kiya slowed",
                        "Arijit Singh hits",
                        "Coke Studio Bangla",
                        "বাংলা নতুন গান 2026"
                    )
                    items(quickTags) { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF222222),
                            modifier = Modifier.clickable {
                                onSearchQueryChange(tag)
                                focusManager.clearFocus()
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = TabInactiveGray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // Live Autocomplete Suggestions from YouTube Suggest API
                if (searchSuggestions.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E1E1E),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            searchSuggestions.take(5).forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSearchQueryChange(suggestion)
                                            focusManager.clearFocus()
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = TabInactiveGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = suggestion,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.NorthWest,
                                        contentDescription = null,
                                        tint = TabInactiveGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // VIDEO FEED
        if (videos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = TabActiveYellow,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "ইউটিউব সার্ভার থেকে ফেচ করা হচ্ছে...",
                        color = TabInactiveGray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
            ) {
                // Section 1: Featured Large Video Cards (Screenshot shows 2 prominent large cards)
                val topFeaturedVideos = videos.take(2)
                val remainingVideos = videos.drop(2)

                items(topFeaturedVideos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        onClick = { onSelectVideo(video) },
                        onDownloadClick = { videoToDownload = video }
                    )
                }

                // Section 2: 2-Column Grid Row (Screenshot shows 2 side-by-side video cards)
                if (remainingVideos.size >= 2) {
                    val gridPair = remainingVideos.take(2)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GridVideoCard(
                                video = gridPair[0],
                                onClick = { onSelectVideo(gridPair[0]) },
                                onDownloadClick = { videoToDownload = gridPair[0] },
                                modifier = Modifier.weight(1f)
                            )
                            GridVideoCard(
                                video = gridPair[1],
                                onClick = { onSelectVideo(gridPair[1]) },
                                onDownloadClick = { videoToDownload = gridPair[1] },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Section 3: Remaining Video Feed
                val restOfVideos = remainingVideos.drop(2)
                items(restOfVideos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        onClick = { onSelectVideo(video) },
                        onDownloadClick = { videoToDownload = video }
                    )
                }
            }
        }
    }
}
