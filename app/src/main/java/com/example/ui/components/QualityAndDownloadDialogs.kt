package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Video
import com.example.model.VideoQuality
import com.example.ui.theme.DataSaverGreen
import com.example.ui.theme.TubeRed

@Composable
fun QualitySelectorDialog(
    currentQuality: VideoQuality,
    isDataSaverEnabled: Boolean,
    onSelectQuality: (VideoQuality) -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HighQuality,
                    contentDescription = null,
                    tint = TubeRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ভিডিও রেজুলেশন ও ডাটা সেভার",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Data Saver Mode Card
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDataSaverEnabled) DataSaverGreen.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleDataSaver(!isDataSaverEnabled) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DataSaverOn,
                                contentDescription = null,
                                tint = if (isDataSaverEnabled) DataSaverGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "আল্ট্রা ডাটা সেভার মোড",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDataSaverEnabled) DataSaverGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "১০০-২০০ এমবি তেই সারাদিন ভিডিও চলবে",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = if (isDataSaverEnabled) DataSaverGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.size(22.dp)
                        ) {
                            if (isDataSaverEnabled) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.padding(3.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = "রেজুলেশন নির্বাচন করুন:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quality Options list
                VideoQuality.entries.forEach { quality ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectQuality(quality)
                                onDismiss()
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentQuality == quality,
                            onClick = {
                                onSelectQuality(quality)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = TubeRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = quality.label,
                                fontSize = 14.sp,
                                fontWeight = if (currentQuality == quality) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val budgetHours = (100.0f / (quality.estimatedMbPerMin * 60.0f))
                            Text(
                                text = "খরচ: ~${quality.estimatedMbPerMin} MB/মিনিট  (100MB দিয়ে ~${String.format("%.1f", budgetHours)} ঘণ্টা চলবে)",
                                fontSize = 11.sp,
                                color = if (quality == VideoQuality.Q144P || quality == VideoQuality.Q240P) DataSaverGreen
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন", color = TubeRed)
            }
        }
    )
}

@Composable
fun FastDownloadDialog(
    video: Video,
    onStartDownload: (VideoQuality) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedQuality by remember { mutableStateOf(VideoQuality.Q240P) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.OfflineBolt,
                    contentDescription = null,
                    tint = TubeRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "দ্রুত ডাউনলোড (Fast Download)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = video.title,
                    fontSize = 13.sp,
                    maxLines = 2,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "ডাউনলোড কোয়ালিটি নির্বাচন করুন:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val downloadOptions = listOf(
                    Triple(VideoQuality.Q240P, "লাইট কোয়ালিটি (Lite 240p) - দ্রুততম", "~4.5 MB • সবচেয়ে কম ডাটা"),
                    Triple(VideoQuality.Q360P, "স্ট্যান্ডার্ড কোয়ালিটি (Standard 360p)", "~11.0 MB • পরিষ্কার দৃশ্য"),
                    Triple(VideoQuality.Q720P, "এইচডি কোয়ালিটি (HD 720p)", "~28.0 MB • সর্বোচ্চ মান")
                )

                downloadOptions.forEach { (q, label, desc) ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedQuality == q) TubeRed.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedQuality = q }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedQuality == q,
                                onClick = { selectedQuality = q },
                                colors = RadioButtonDefaults.colors(selectedColor = TubeRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedQuality == q) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = if (q == VideoQuality.Q240P) DataSaverGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStartDownload(selectedQuality)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TubeRed),
                modifier = Modifier.testTag("confirm_download_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("ডাউনলোড শুরু করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
