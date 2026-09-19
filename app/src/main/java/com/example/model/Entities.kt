package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_interactions")
data class UserInteractionEntity(
    @PrimaryKey val videoId: String,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val likeCountOffset: Int = 0,
    val lastWatchedPositionMs: Long = 0L,
    val watchTimestamp: Long = 0L,
    val isWatchHistory: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val commentText: String,
    val timestamp: Long,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val channelName: String,
    val channelAvatarUrl: String,
    val subscriberCount: String,
    val isSubscribed: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val subscribedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadedVideoEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val title: String,
    val channelName: String,
    val channelAvatarUrl: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val localFilePath: String,
    val quality: String,
    val fileSizeBytes: Long,
    val durationSeconds: Long,
    val progress: Int, // 0 to 100
    val isCompleted: Boolean,
    val downloadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "data_saver_settings")
data class DataSaverSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val dataSaverEnabled: Boolean = true,
    val defaultQuality: String = "auto",
    val dailyBudgetMb: Int = 150,
    val usedMbToday: Float = 14.5f,
    val lastResetDate: String = "2026-09-19",
    val autoPlayNext: Boolean = false
)
