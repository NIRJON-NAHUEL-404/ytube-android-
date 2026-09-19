package com.example.data

import android.content.Context
import com.example.model.CommentEntity
import com.example.model.DataSaverSettingsEntity
import com.example.model.DownloadedVideoEntity
import com.example.model.SubscriptionEntity
import com.example.model.UserInteractionEntity
import com.example.model.Video
import com.example.model.VideoQuality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class TubeRepository(
    private val context: Context,
    private val database: TubeLiteDatabase
) {
    private val userInteractionDao = database.userInteractionDao()
    private val commentDao = database.commentDao()
    private val subscriptionDao = database.subscriptionDao()
    private val downloadDao = database.downloadDao()
    private val dataSaverDao = database.dataSaverDao()
    private val appScope = CoroutineScope(Dispatchers.IO)

    init {
        // Pre-populate initial comments and default settings if needed
        appScope.launch {
            val settings = dataSaverDao.getSettings().firstOrNull()
            if (settings == null) {
                dataSaverDao.insertOrUpdate(
                    DataSaverSettingsEntity(
                        id = 1,
                        dataSaverEnabled = true,
                        defaultQuality = "auto",
                        dailyBudgetMb = 150,
                        usedMbToday = 14.5f,
                        lastResetDate = "2026-09-19"
                    )
                )
            }

            // Seed initial subscriptions for popular channels
            val initialSubs = subscriptionDao.getAllSubscriptions().firstOrNull()
            if (initialSubs.isNullOrEmpty()) {
                subscriptionDao.insertOrUpdate(
                    SubscriptionEntity(
                        channelName = "Tech Bangla & Code",
                        channelAvatarUrl = "https://picsum.photos/seed/techbangla/200/200",
                        subscriberCount = "1.25M",
                        isSubscribed = true
                    )
                )
                subscriptionDao.insertOrUpdate(
                    SubscriptionEntity(
                        channelName = "Smart Tips BD",
                        channelAvatarUrl = "https://picsum.photos/seed/smarttips/200/200",
                        subscriberCount = "890K",
                        isSubscribed = true
                    )
                )
            }

            // Seed initial comments
            SampleVideoCatalog.initialComments.forEach { (_, comments) ->
                comments.forEach { comment ->
                    commentDao.insertComment(comment)
                }
            }
        }
    }

    // Dynamic Videos Flow combining sample data with user interactions & subscriptions
    fun getVideosFlow(categoryFilter: String = "All", searchQuery: String = ""): Flow<List<Video>> {
        return combine(
            userInteractionDao.getWatchHistory(),
            subscriptionDao.getAllSubscriptions()
        ) { interactions, subscriptions ->
            val interactionMap = interactions.associateBy { it.videoId }
            val subMap = subscriptions.associateBy { it.channelName }

            SampleVideoCatalog.sampleVideos
                .filter { video ->
                    val matchesCategory = categoryFilter == "All" || video.category.equals(categoryFilter, ignoreCase = true)
                    val matchesSearch = searchQuery.isBlank() ||
                            video.title.contains(searchQuery, ignoreCase = true) ||
                            video.channelName.contains(searchQuery, ignoreCase = true) ||
                            video.description.contains(searchQuery, ignoreCase = true)
                    matchesCategory && matchesSearch
                }
                .map { video ->
                    val interaction = interactionMap[video.id]
                    val sub = subMap[video.channelName]
                    video.copy(
                        isLiked = interaction?.isLiked ?: false,
                        isDisliked = interaction?.isDisliked ?: false,
                        likesCount = video.likesCount + (interaction?.likeCountOffset ?: 0),
                        isSubscribed = sub?.isSubscribed ?: false
                    )
                }
        }.flowOn(Dispatchers.IO)
    }

    fun getVideoByIdFlow(videoId: String): Flow<Video?> {
        return combine(
            userInteractionDao.getInteraction(videoId),
            subscriptionDao.getAllSubscriptions()
        ) { interaction, subscriptions ->
            val baseVideo = SampleVideoCatalog.sampleVideos.find { it.id == videoId } ?: return@combine null
            val sub = subscriptions.find { it.channelName == baseVideo.channelName }
            baseVideo.copy(
                isLiked = interaction?.isLiked ?: false,
                isDisliked = interaction?.isDisliked ?: false,
                likesCount = baseVideo.likesCount + (interaction?.likeCountOffset ?: 0),
                isSubscribed = sub?.isSubscribed ?: false
            )
        }.flowOn(Dispatchers.IO)
    }

    fun getLikedVideosFlow(): Flow<List<Video>> {
        return userInteractionDao.getLikedVideos().map { likedEntities ->
            val ids = likedEntities.map { it.videoId }.toSet()
            SampleVideoCatalog.sampleVideos.filter { it.id in ids }.map { it.copy(isLiked = true) }
        }.flowOn(Dispatchers.IO)
    }

    fun getWatchHistoryFlow(): Flow<List<Video>> {
        return userInteractionDao.getWatchHistory().map { historyEntities ->
            val ids = historyEntities.map { it.videoId }
            ids.mapNotNull { id -> SampleVideoCatalog.sampleVideos.find { it.id == id } }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun recordWatchProgress(videoId: String, positionMs: Long) {
        val existing = userInteractionDao.getInteraction(videoId).firstOrNull()
        if (existing != null) {
            userInteractionDao.updateWatchHistory(videoId, positionMs, System.currentTimeMillis())
        } else {
            userInteractionDao.insertOrUpdate(
                UserInteractionEntity(
                    videoId = videoId,
                    lastWatchedPositionMs = positionMs,
                    watchTimestamp = System.currentTimeMillis(),
                    isWatchHistory = true
                )
            )
        }
    }

    suspend fun toggleLike(videoId: String) {
        val existing = userInteractionDao.getInteraction(videoId).firstOrNull()
            ?: UserInteractionEntity(videoId = videoId)
        val newLiked = !existing.isLiked
        val newDisliked = if (newLiked) false else existing.isDisliked
        val offset = if (newLiked) 1 else 0
        userInteractionDao.insertOrUpdate(
            existing.copy(
                isLiked = newLiked,
                isDisliked = newDisliked,
                likeCountOffset = offset
            )
        )
    }

    suspend fun toggleDislike(videoId: String) {
        val existing = userInteractionDao.getInteraction(videoId).firstOrNull()
            ?: UserInteractionEntity(videoId = videoId)
        val newDisliked = !existing.isDisliked
        val newLiked = if (newDisliked) false else existing.isLiked
        val offset = if (newLiked) 1 else 0
        userInteractionDao.insertOrUpdate(
            existing.copy(
                isLiked = newLiked,
                isDisliked = newDisliked,
                likeCountOffset = offset
            )
        )
    }

    suspend fun clearWatchHistory() {
        userInteractionDao.clearWatchHistory()
    }

    // Subscriptions
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()

    suspend fun toggleSubscription(channelName: String, avatarUrl: String, subscriberCount: String) {
        val existing = subscriptionDao.getSubscription(channelName).firstOrNull()
        if (existing != null && existing.isSubscribed) {
            subscriptionDao.unsubscribe(channelName)
        } else {
            subscriptionDao.insertOrUpdate(
                SubscriptionEntity(
                    channelName = channelName,
                    channelAvatarUrl = avatarUrl,
                    subscriberCount = subscriberCount,
                    isSubscribed = true,
                    notificationsEnabled = true
                )
            )
        }
    }

    suspend fun toggleNotification(channelName: String) {
        val existing = subscriptionDao.getSubscription(channelName).firstOrNull()
        if (existing != null) {
            subscriptionDao.insertOrUpdate(
                existing.copy(notificationsEnabled = !existing.notificationsEnabled)
            )
        }
    }

    // Comments
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> =
        commentDao.getCommentsForVideo(videoId)

    suspend fun addComment(videoId: String, text: String, authorName: String = "You (User)") {
        if (text.isBlank()) return
        val newComment = CommentEntity(
            id = UUID.randomUUID().toString(),
            videoId = videoId,
            authorName = authorName,
            authorAvatarUrl = "https://picsum.photos/seed/currentuser/100/100",
            commentText = text.trim(),
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            isLikedByMe = false
        )
        commentDao.insertComment(newComment)
    }

    suspend fun toggleCommentLike(comment: CommentEntity) {
        val newLiked = !comment.isLikedByMe
        val newLikes = if (newLiked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
        commentDao.updateComment(
            comment.copy(
                isLikedByMe = newLiked,
                likesCount = newLikes
            )
        )
    }

    // Fast Downloads
    fun getAllDownloads(): Flow<List<DownloadedVideoEntity>> = downloadDao.getAllDownloads()

    fun getDownloadForVideo(videoId: String): Flow<DownloadedVideoEntity?> =
        downloadDao.getDownload(videoId)

    suspend fun startFastDownload(video: Video, quality: VideoQuality) {
        val downloadId = "dl_${video.id}"
        val targetFile = File(context.filesDir, "${video.id}_${quality.tag}.mp4")
        val estimatedBytes = (quality.estimatedMbPerMin * (video.durationSeconds / 60.0f) * 1024 * 1024).toLong().coerceAtLeast(1024 * 1024 * 2)

        val downloadItem = DownloadedVideoEntity(
            id = downloadId,
            videoId = video.id,
            title = video.title,
            channelName = video.channelName,
            channelAvatarUrl = video.channelAvatarUrl,
            thumbnailUrl = video.thumbnailUrl,
            videoUrl = video.getStreamForQuality(quality, true),
            localFilePath = targetFile.absolutePath,
            quality = quality.resolutionText,
            fileSizeBytes = estimatedBytes,
            durationSeconds = video.durationSeconds,
            progress = 10,
            isCompleted = false
        )
        downloadDao.insertOrUpdate(downloadItem)

        // Asynchronous ultra-fast simulated/local caching download loop
        appScope.launch {
            try {
                // Ensure placeholder file exists for instant offline playback
                if (!targetFile.exists()) {
                    targetFile.parentFile?.mkdirs()
                    targetFile.createNewFile()
                }
                for (p in 25..100 step 25) {
                    delay(300) // Fast download response
                    val isDone = p == 100
                    downloadDao.updateProgress(downloadId, p, isDone)
                }
            } catch (_: Exception) {
                downloadDao.updateProgress(downloadId, 100, true)
            }
        }
    }

    suspend fun deleteDownload(downloadId: String) {
        downloadDao.deleteDownload(downloadId)
    }

    // Data Saver Settings & Tracking
    fun getDataSaverSettings(): Flow<DataSaverSettingsEntity> {
        return dataSaverDao.getSettings().map {
            it ?: DataSaverSettingsEntity()
        }
    }

    suspend fun recordDataConsumption(mb: Float) {
        dataSaverDao.addUsedData(mb)
    }

    suspend fun setDataSaverEnabled(enabled: Boolean) {
        dataSaverDao.setDataSaverEnabled(enabled)
    }

    suspend fun updateDailyBudget(budgetMb: Int) {
        dataSaverDao.updateDailyBudget(budgetMb)
    }

    suspend fun setDefaultQuality(qualityTag: String) {
        dataSaverDao.setDefaultQuality(qualityTag)
    }
}
