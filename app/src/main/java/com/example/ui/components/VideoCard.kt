package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.Video

/**
 * Clean, modern video card matching the exact UI from user's screenshot:
 * - Edge-to-edge / slightly rounded high-res thumbnail (16:9)
 * - Mix / Playlist icon indicator on bottom-right of thumbnail
 * - Left: Title in bold white, Artists / Channel subtitle below
 * - Right: Direct Download button (arrow into tray)
 */
@Composable
fun VideoCard(
    video: Video,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("video_card_${video.id}")
            .padding(bottom = 18.dp)
    ) {
        // Thumbnail Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9.2f)
                .background(Color(0xFF181818))
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // Playlist / Mix 3-lines icon on bottom-right (as shown in screenshot)
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Mix",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    if (video.durationSeconds > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = video.getFormattedDuration(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Info Row: Left Title & Artist / Subtitle, Right Direct Download Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Title
                Text(
                    text = video.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Artist / Channel Subtitle
                Text(
                    text = video.channelName,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Direct Download Button (arrow down into tray, as shown in screenshot)
            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("download_button_${video.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = "Download Video",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

/**
 * 2-Column Grid Video Card for bottom section
 */
@Composable
fun GridVideoCard(
    video: Video,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E1E1E))
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
            ) {
                Text(
                    text = video.getFormattedDuration(),
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = video.channelName,
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = "Download",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
