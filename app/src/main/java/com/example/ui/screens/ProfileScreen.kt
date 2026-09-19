package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.model.DataSaverSettingsEntity
import com.example.model.Video
import com.example.ui.theme.DataSaverGreen
import com.example.ui.theme.TubeRed

@Composable
fun ProfileScreen(
    dataSaverSettings: DataSaverSettingsEntity,
    watchHistory: List<Video>,
    likedVideos: List<Video>,
    downloadCount: Int,
    onSelectVideo: (Video) -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    onSetDailyBudget: (Int) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var budgetSliderValue by remember(dataSaverSettings.dailyBudgetMb) {
        mutableFloatStateOf(dataSaverSettings.dailyBudgetMb.toFloat())
    }

    val usedMb = dataSaverSettings.usedMbToday
    val budgetMb = dataSaverSettings.dailyBudgetMb
    val remainingMb = (budgetMb - usedMb).coerceAtLeast(0f)
    val remainingMinutes = (remainingMb / 0.8f).toInt() // At ultra saver rate (0.8 MB/min)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Profile Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://picsum.photos/seed/currentuser/150/150",
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333))
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "TubeLite ব্যবহারকারী",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "@user_tubelite • প্রোফাইল",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DataSaverGreen.copy(alpha = 0.15f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "⚡ স্মার্ট ডাটা সেভার অ্যাক্টিভ",
                            fontSize = 11.sp,
                            color = DataSaverGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "দেখা হয়েছে",
                    value = "${watchHistory.size}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "ডাটা সাশ্রয়",
                    value = "~${String.format("%.0f", usedMb * 3.5f)} MB",
                    accentColor = DataSaverGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "ডাউনলোড",
                    value = "$downloadCount",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "লাইক",
                    value = "${likedVideos.size}",
                    accentColor = TubeRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Data Saver Control Hub Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DataSaverOn,
                                contentDescription = null,
                                tint = DataSaverGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "আল্ট্রা ডাটা সেভার সেটিংস",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "১০০-২০০ এমবিতে সারাদিন ভিডিওর নিশ্চয়তা",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = dataSaverSettings.dataSaverEnabled,
                            onCheckedChange = onToggleDataSaver,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DataSaverGreen
                            ),
                            modifier = Modifier.testTag("profile_data_saver_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Daily Budget Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "দৈনিক ডাটা বাজেট:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${budgetSliderValue.toInt()} MB / দিন",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DataSaverGreen
                        )
                    }

                    Slider(
                        value = budgetSliderValue,
                        onValueChange = { budgetSliderValue = it },
                        onValueChangeFinished = {
                            onSetDailyBudget(budgetSliderValue.toInt())
                        },
                        valueRange = 50f..500f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = DataSaverGreen,
                            activeTrackColor = DataSaverGreen
                        ),
                        modifier = Modifier.testTag("daily_budget_slider")
                    )

                    // Consumption Progress Bar
                    val usageRatio = (usedMb / budgetMb).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { usageRatio },
                        color = if (usageRatio > 0.85f) TubeRed else DataSaverGreen,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ব্যবহৃত: ${String.format("%.1f", usedMb)} MB",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "অবশিষ্ট: ~${remainingMinutes} মিনিট দেখা যাবে",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DataSaverGreen
                        )
                    }
                }
            }
        }

        // Watch History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TubeRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ইতিহাস (Watch History)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (watchHistory.isNotEmpty()) {
                    TextButton(onClick = onClearHistory) {
                        Text("মুছে ফেলুন", color = TubeRed, fontSize = 12.sp)
                    }
                }
            }

            if (watchHistory.isEmpty()) {
                Text(
                    text = "কোনো দেখার ইতিহাস নেই",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(watchHistory, key = { it.id }) { v ->
                        Column(
                            modifier = Modifier
                                .width(130.dp)
                                .clickable { onSelectVideo(v) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF222222))
                            ) {
                                AsyncImage(
                                    model = v.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = v.title,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Liked Videos
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = null,
                    tint = TubeRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "পছন্দের ভিডিওসমূহ (Liked Videos)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (likedVideos.isEmpty()) {
                Text(
                    text = "কোনো লাইক করা ভিডিও নেই",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(likedVideos, key = { it.id }) { v ->
                        Column(
                            modifier = Modifier
                                .width(130.dp)
                                .clickable { onSelectVideo(v) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF222222))
                            ) {
                                AsyncImage(
                                    model = v.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = v.title,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    accentColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
