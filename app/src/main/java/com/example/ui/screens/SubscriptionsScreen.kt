package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.model.SubscriptionEntity
import com.example.model.Video
import com.example.model.VideoQuality
import com.example.ui.components.FastDownloadDialog
import com.example.ui.components.VideoCard
import com.example.ui.theme.TubeRed

@Composable
fun SubscriptionsScreen(
    subscriptions: List<SubscriptionEntity>,
    allVideos: List<Video>,
    onSelectVideo: (Video) -> Unit,
    onStartDownload: (Video, VideoQuality) -> Unit,
    onToggleSubscribe: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var videoToDownload by remember { mutableStateOf<Video?>(null) }

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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Subscriptions,
                contentDescription = null,
                tint = TubeRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "সাবস্ক্রিপশন (Subscriptions)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Horizontal Channels Row
        if (subscriptions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                subscriptions.forEach { sub ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(68.dp)
                            .clickable {
                                onToggleSubscribe(sub.channelName, sub.channelAvatarUrl, sub.subscriberCount)
                            }
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = sub.channelAvatarUrl,
                                contentDescription = sub.channelName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF333333))
                            )
                            Surface(
                                shape = CircleShape,
                                color = TubeRed,
                                modifier = Modifier.size(14.dp)
                            ) {}
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = sub.channelName,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }

        // Subscribed Feed or Suggestion state
        val subscribedChannelNames = subscriptions.map { it.channelName }.toSet()
        val feedVideos = allVideos.filter { it.channelName in subscribedChannelNames }

        if (feedVideos.isEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Icon(
                        imageVector = Icons.Default.Subscriptions,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "নতুন ভিডিও দেখতে চ্যানেল সাবস্ক্রাইব করুন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আপনার প্রিয় নির্মাতাদের চ্যানেল সাবস্ক্রাইব করলে তাদের সব ভিডিও এখানে দেখতে পাবেন।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "জনপ্রিয় চ্যানেলসমূহ:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val creators = listOf(
                    Triple("Tech Bangla & Code", "https://picsum.photos/seed/techbangla/200/200", "1.25M"),
                    Triple("Smart Tips BD", "https://picsum.photos/seed/smarttips/200/200", "890K"),
                    Triple("Bangla Explorer", "https://picsum.photos/seed/explorer/200/200", "450K"),
                    Triple("Lofi Vibes Studio", "https://picsum.photos/seed/lofistudio/200/200", "3.4M")
                )

                items(creators) { (name, avatar, count) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = avatar,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$count সাবস্ক্রাইবার",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { onToggleSubscribe(name, avatar, count) },
                                colors = ButtonDefaults.buttonColors(containerColor = TubeRed)
                            ) {
                                Text("Subscribe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(feedVideos, key = { it.id }) { video ->
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
