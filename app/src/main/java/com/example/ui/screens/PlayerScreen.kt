package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CommentEntity
import com.example.model.Video
import com.example.model.VideoQuality
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.FastDownloadDialog
import com.example.ui.components.QualitySelectorDialog
import com.example.ui.components.VideoCard
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.DataSaverGreen
import com.example.ui.theme.TubeRed

@Composable
fun PlayerScreen(
    video: Video,
    allVideos: List<Video>,
    comments: List<CommentEntity>,
    selectedQuality: VideoQuality,
    isDataSaverEnabled: Boolean,
    isFullScreen: Boolean,
    onClosePlayer: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onSelectQuality: (VideoQuality) -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    onToggleLike: (Video) -> Unit,
    onToggleDislike: (Video) -> Unit,
    onToggleSubscribe: (channelName: String, avatarUrl: String, count: String) -> Unit,
    onAddComment: (videoId: String, text: String) -> Unit,
    onLikeComment: (CommentEntity) -> Unit,
    onStartDownload: (video: Video, quality: VideoQuality) -> Unit,
    onSelectVideo: (Video) -> Unit,
    onPlaybackTick: (seconds: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQualityDialog by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    if (showQualityDialog) {
        QualitySelectorDialog(
            currentQuality = selectedQuality,
            isDataSaverEnabled = isDataSaverEnabled,
            onSelectQuality = onSelectQuality,
            onToggleDataSaver = onToggleDataSaver,
            onDismiss = { showQualityDialog = false }
        )
    }

    if (showDownloadDialog) {
        FastDownloadDialog(
            video = video,
            onStartDownload = { q -> onStartDownload(video, q) },
            onDismiss = { showDownloadDialog = false }
        )
    }

    if (showCommentsSheet) {
        CommentsBottomSheet(
            comments = comments,
            onAddComment = { text -> onAddComment(video.id, text) },
            onLikeComment = onLikeComment,
            onDismiss = { showCommentsSheet = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Player Component
        val playerModifier = if (isFullScreen) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        }

        VideoPlayerView(
            video = video,
            quality = selectedQuality,
            isDataSaverEnabled = isDataSaverEnabled,
            isFullScreen = isFullScreen,
            onClosePlayer = onClosePlayer,
            onToggleFullScreen = onToggleFullScreen,
            onOpenQualitySettings = { showQualityDialog = true },
            onPlaybackTick = onPlaybackTick,
            modifier = playerModifier
        )

        // If Fullscreen, only player is shown
        if (!isFullScreen) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Title and Meta Info
                item {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(
                            text = video.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = video.getFormattedViews(),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = video.uploadedTimeAgo,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isDataSaverEnabled) DataSaverGreen.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (isDataSaverEnabled) "⚡ সেভার চালু" else "স্ট্যান্ডার্ড",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDataSaverEnabled) DataSaverGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Channel Info & Subscribe Button
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            AsyncImage(
                                model = video.channelAvatarUrl,
                                contentDescription = video.channelName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF333333))
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = video.channelName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "${video.subscriberCount} সাবস্ক্রাইবার",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Subscribe Button
                        Button(
                            onClick = {
                                onToggleSubscribe(video.channelName, video.channelAvatarUrl, video.subscriberCount)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (video.isSubscribed) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.onBackground,
                                contentColor = if (video.isSubscribed) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.background
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("subscribe_button")
                        ) {
                            if (video.isSubscribed) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = TubeRed
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Subscribed", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            } else {
                                Text(text = "Subscribe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Action Bar: Like, Dislike, Download, Comments, Share
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like Button
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { onToggleLike(video) }
                                        .testTag("like_button")
                                ) {
                                    Icon(
                                        imageVector = if (video.isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                        contentDescription = "Like",
                                        tint = if (video.isLiked) TubeRed else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${video.likesCount}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(14.dp)
                                        .background(MaterialTheme.colorScheme.outline)
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                // Dislike
                                Icon(
                                    imageVector = if (video.isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                                    contentDescription = "Dislike",
                                    tint = if (video.isDisliked) TubeRed else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { onToggleDislike(video) }
                                        .testTag("dislike_button")
                                )
                            }
                        }

                        // Fast Download Button
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TubeRed.copy(alpha = 0.12f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showDownloadDialog = true }
                                .testTag("player_download_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = TubeRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ডাউনলোড",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TubeRed
                                )
                            }
                        }

                        // Comments Button
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showCommentsSheet = true }
                                .testTag("open_comments_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Comment,
                                    contentDescription = "Comments",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${comments.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Quality / Data Saver
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showQualityDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Quality",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Description Box (Collapsible)
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                            .animateContentSize()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = video.description,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = if (isDescriptionExpanded) 50 else 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isDescriptionExpanded) "...কম দেখুন (Show less)" else "...আরও দেখুন (Show more)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Comments Preview Section
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showCommentsSheet = true }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "মন্তব্য (${comments.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "সব দেখুন",
                                    fontSize = 12.sp,
                                    color = TubeRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (comments.isNotEmpty()) {
                                val topComment = comments.first()
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = topComment.authorAvatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = topComment.commentText,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = "পরবর্তী ভিডিওসমূহ (Up Next)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                // Related Videos List
                val related = allVideos.filter { it.id != video.id }
                items(related, key = { it.id }) { nextVideo ->
                    VideoCard(
                        video = nextVideo,
                        onClick = { onSelectVideo(nextVideo) },
                        onDownloadClick = {
                            onStartDownload(nextVideo, VideoQuality.Q240P)
                        }
                    )
                }
            }
        }
    }
}
