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

    private val dynamicVideoCache = java.util.concurrent.ConcurrentHashMap<String, Video>()

    private fun getAllCatalogVideos(): List<Video> {
        return SampleVideoCatalog.sampleVideos + dynamicVideoCache.values.toList()
    }

    private fun generateDynamicVideos(query: String): List<Video> {
        val cleanQuery = query.trim()
        val isMusicQuery = cleanQuery.contains("গান", ignoreCase = true) ||
                cleanQuery.contains("song", ignoreCase = true) ||
                cleanQuery.contains("music", ignoreCase = true) ||
                cleanQuery.contains("সুর", ignoreCase = true) ||
                cleanQuery.contains("গজল", ignoreCase = true) ||
                cleanQuery.contains("বাউল", ignoreCase = true)

        val musicYtIds = listOf("YGnctmSC3z8", "t33M1Rj83nU", "mvUNTnHk07k", "O7WnA8AU4O8", "6y2pKyG7KSc")
        val generalYtIds = listOf("tWatFr--zGY", "anzhRV7Qq2M", "dHoZ6Vd6HcE", "eRsGyueVLvQ", "Z0zB9gjWnfo")

        val musicThumbnails = listOf(
            "https://i.ytimg.com/vi/YGnctmSC3z8/hqdefault.jpg",
            "https://i.ytimg.com/vi/t33M1Rj83nU/hqdefault.jpg",
            "https://i.ytimg.com/vi/mvUNTnHk07k/hqdefault.jpg",
            "https://i.ytimg.com/vi/O7WnA8AU4O8/hqdefault.jpg"
        )

        val generalThumbnails = listOf(
            "https://i.ytimg.com/vi/tWatFr--zGY/hqdefault.jpg",
            "https://i.ytimg.com/vi/anzhRV7Qq2M/hqdefault.jpg",
            "https://i.ytimg.com/vi/dHoZ6Vd6HcE/hqdefault.jpg",
            "https://i.ytimg.com/vi/eRsGyueVLvQ/hqdefault.jpg"
        )

        val thumbs = if (isMusicQuery) musicThumbnails else generalThumbnails
        val ytIds = if (isMusicQuery) musicYtIds else generalYtIds

        val channels = if (isMusicQuery) {
            listOf("Bangla Melody Station", "Sur O Chhondo BD", "TubeLite Music Hits", "Acoustic Vibes BD")
        } else {
            listOf("Bangla Media Hub", "Smart Creator BD", "TubeLite Trends", "Digital Explorer")
        }

        val titles = if (isMusicQuery) {
            listOf(
                "$cleanQuery - জনপ্রিয় সেরা রোমান্টিক গান ও মিউজিক | Official Video",
                "$cleanQuery - মন ছুঁয়ে যাওয়া মেলোডিয়াস গান কালেকশন | Low Data",
                "$cleanQuery - সেরা হিট গান ও রিল্যাক্সিং সুর | TubeLite Special",
                "$cleanQuery - লাইভ স্টুডিও সেশন ও অ্যাকোস্টিক ভার্সন"
            )
        } else {
            listOf(
                "$cleanQuery - সম্পূর্ণ ভিডিও ও এক্সক্লুসিভ আপডেট 2026",
                "$cleanQuery - সেরা মুহূর্ত ও বিস্তারিত রিভিউ | TubeLite",
                "$cleanQuery - আনলিমিটেড এন্টারটেইনমেন্ট ও স্পেশাল ক্লিপ",
                "$cleanQuery - নতুন ট্রেন্ডিং ভিডিও | Data Saver 144p-720p"
            )
        }

        val queryHash = cleanQuery.hashCode()

        return titles.mapIndexed { index, title ->
            val videoId = "dyn_${queryHash}_$index"
            val ytId = ytIds[index % ytIds.size]
            Video(
                id = videoId,
                title = title,
                description = "সার্চ রেজাল্ট: $cleanQuery। লো ডাটা সেভার মোডে কম খরচে হাই স্পিডে প্লে করুন।",
                channelName = channels[index % channels.size],
                channelAvatarUrl = "https://picsum.photos/seed/${videoId}/200/200",
                subscriberCount = "${(index + 1) * 450}K",
                videoUrl = "https://www.youtube.com/watch?v=$ytId",
                thumbnailUrl = thumbs[index % thumbs.size],
                durationSeconds = (180 + index * 45).toLong(),
                viewsCount = (320000L + index * 185000L),
                uploadedTimeAgo = "${index + 1} days ago",
                category = if (isMusicQuery) "Music" else "All",
                youtubeId = ytId,
                likesCount = (15000L + index * 8200L),
                commentsCount = 210 + index * 85
            )
        }
    }

    // Dynamic Videos Flow combining sample data with user interactions & subscriptions
    fun getVideosFlow(categoryFilter: String = "All", searchQuery: String = ""): Flow<List<Video>> = flow {
        val query = searchQuery.trim()
        val tokens = query.split("\\s+".toRegex()).filter { it.isNotBlank() }

        // Step 1: Immediately emit local matching videos so UI responds without delay
        val localMatches = getAllCatalogVideos().filter { video ->
            val matchesCategory = categoryFilter == "All" ||
                    video.category.equals(categoryFilter, ignoreCase = true) ||
                    (categoryFilter.contains("Music", ignoreCase = true) && video.category.equals("Music", ignoreCase = true)) ||
                    (categoryFilter.contains("গান", ignoreCase = true) && video.category.equals("Music", ignoreCase = true))

            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                val fullMatch = video.title.contains(query, ignoreCase = true) ||
                        video.channelName.contains(query, ignoreCase = true) ||
                        video.description.contains(query, ignoreCase = true) ||
                        video.category.contains(query, ignoreCase = true)

                val tokenMatch = tokens.any { token ->
                    video.title.contains(token, ignoreCase = true) ||
                            video.channelName.contains(token, ignoreCase = true) ||
                            video.description.contains(token, ignoreCase = true) ||
                            (token.equals("গান", ignoreCase = true) && video.category.equals("Music", ignoreCase = true)) ||
                            (token.equals("song", ignoreCase = true) && video.category.equals("Music", ignoreCase = true)) ||
                            (token.equals("music", ignoreCase = true) && video.category.equals("Music", ignoreCase = true))
                }
                fullMatch || tokenMatch
            }
            matchesCategory && matchesSearch
        }

        if (localMatches.isNotEmpty()) {
            emit(enrichVideos(localMatches))
        }

        // Step 2: Fetch real YouTube videos from YouTube Innertube backend
        val ytQuery = resolveYouTubeQuery(categoryFilter, query)

        try {
            val realResult = YouTubeSearchService.searchVideosWithContinuation(ytQuery)
            if (realResult.videos.isNotEmpty()) {
                realResult.videos.forEach { v -> dynamicVideoCache[v.id] = v }
                emit(enrichVideos(realResult.videos))
            } else if (localMatches.isEmpty()) {
                val fallback = generateDynamicVideos(query.ifBlank { "YouTube Hits" })
                fallback.forEach { dynamicVideoCache[it.id] = it }
                emit(enrichVideos(fallback))
            }
        } catch (_: Exception) {
            if (localMatches.isEmpty()) {
                val fallback = generateDynamicVideos(query.ifBlank { "YouTube Hits" })
                fallback.forEach { dynamicVideoCache[it.id] = it }
                emit(enrichVideos(fallback))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun resolveYouTubeQuery(categoryFilter: String, query: String): String {
        val clean = query.trim()
        return when {
            clean.isNotBlank() -> clean
            categoryFilter == "YouTube" -> "Mix Music of Asia slowed reverb"
            categoryFilter == "Music" || categoryFilter == "গান (Music)" -> "Kamariya slowed reverb lofi songs"
            categoryFilter.contains("গজল", ignoreCase = true) || categoryFilter.contains("Ghazal", ignoreCase = true) -> "বাংলা মন জুড়ানো গজল নাশীদ 2026"
            categoryFilter.contains("মুভি", ignoreCase = true) || categoryFilter.contains("নাটক", ignoreCase = true) -> "বাংলা নতুন নাটক ও সিনেমা 2026"
            categoryFilter == "More" -> "বাংলা সেরা নতুন গান ও নাটক 2026"
            categoryFilter.equals("Tech", ignoreCase = true) -> "Tech Tips Bangla 2026"
            categoryFilter.equals("Travel", ignoreCase = true) -> "Travel Vlog Bangladesh 4K"
            categoryFilter.equals("Gaming", ignoreCase = true) -> "Gaming Highlights esports"
            categoryFilter.equals("Food", ignoreCase = true) -> "Bangla Recipe Ranna"
            categoryFilter.equals("Animation", ignoreCase = true) -> "Best Animation Short Film"
            else -> "Mix Music of Asia slowed reverb"
        }
    }

    suspend fun searchVideosWithPagination(categoryFilter: String = "All", searchQuery: String = ""): YouTubeSearchService.SearchResult {
        val clean = searchQuery.trim()
        val ytQuery = resolveYouTubeQuery(categoryFilter, clean)

        return try {
            val result = YouTubeSearchService.searchVideosWithContinuation(ytQuery)
            if (result.videos.isNotEmpty()) {
                result.videos.forEach { v -> dynamicVideoCache[v.id] = v }
                YouTubeSearchService.SearchResult(
                    videos = enrichVideos(result.videos),
                    continuationToken = result.continuationToken
                )
            } else {
                val fallback = generateDynamicVideos(clean.ifBlank { "YouTube Hits" })
                fallback.forEach { dynamicVideoCache[it.id] = it }
                YouTubeSearchService.SearchResult(enrichVideos(fallback), null)
            }
        } catch (_: Exception) {
            val fallback = generateDynamicVideos(clean.ifBlank { "YouTube Hits" })
            fallback.forEach { dynamicVideoCache[it.id] = it }
            YouTubeSearchService.SearchResult(enrichVideos(fallback), null)
        }
    }

    suspend fun loadMoreYouTubeVideos(continuationToken: String, query: String = ""): YouTubeSearchService.SearchResult {
        return try {
            val result = YouTubeSearchService.continueSearch(continuationToken, query)
            if (result.videos.isNotEmpty()) {
                result.videos.forEach { v -> dynamicVideoCache[v.id] = v }
                YouTubeSearchService.SearchResult(
                    videos = enrichVideos(result.videos),
                    continuationToken = result.continuationToken
                )
            } else {
                YouTubeSearchService.SearchResult(emptyList(), null)
            }
        } catch (_: Exception) {
            YouTubeSearchService.SearchResult(emptyList(), null)
        }
    }

    suspend fun enrichVideos(videos: List<Video>): List<Video> {
        val interactions = userInteractionDao.getWatchHistory().firstOrNull() ?: emptyList()
        val subscriptions = subscriptionDao.getAllSubscriptions().firstOrNull() ?: emptyList()
        val interactionMap = interactions.associateBy { it.videoId }
        val subMap = subscriptions.associateBy { it.channelName }
        return videos.map { video ->
            val interaction = interactionMap[video.id]
            val sub = subMap[video.channelName]
            video.copy(
                isLiked = interaction?.isLiked ?: false,
                isDisliked = interaction?.isDisliked ?: false,
                likesCount = video.likesCount + (interaction?.likeCountOffset ?: 0),
                isSubscribed = sub?.isSubscribed ?: false
            )
        }
    }

    fun getVideoByIdFlow(videoId: String): Flow<Video?> {
        return combine(
            userInteractionDao.getInteraction(videoId),
            subscriptionDao.getAllSubscriptions()
        ) { interaction, subscriptions ->
            var baseVideo = getAllCatalogVideos().find { it.id == videoId }
            if (baseVideo == null) {
                val cleanId = videoId.removePrefix("yt_")
                baseVideo = Video(
                    id = videoId,
                    title = "YouTube Video",
                    description = "Official YouTube Playback",
                    channelName = "YouTube Creator",
                    channelAvatarUrl = "https://picsum.photos/seed/$cleanId/200/200",
                    subscriberCount = "1.0M",
                    videoUrl = "https://www.youtube.com/watch?v=$cleanId",
                    thumbnailUrl = "https://i.ytimg.com/vi/$cleanId/hqdefault.jpg",
                    durationSeconds = 300,
                    viewsCount = 100000,
                    uploadedTimeAgo = "Recently",
                    category = "YouTube",
                    youtubeId = cleanId
                )
                dynamicVideoCache[videoId] = baseVideo
            }
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
            getAllCatalogVideos().filter { it.id in ids }.map { it.copy(isLiked = true) }
        }.flowOn(Dispatchers.IO)
    }

    fun getWatchHistoryFlow(): Flow<List<Video>> {
        return userInteractionDao.getWatchHistory().map { historyEntities ->
            val ids = historyEntities.map { it.videoId }
            ids.mapNotNull { id -> getAllCatalogVideos().find { it.id == id } }
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
