package com.example.model

enum class VideoQuality(
    val label: String,
    val resolutionText: String,
    val estimatedMbPerMin: Float,
    val tag: String
) {
    AUTO("Auto (Adaptive)", "Auto", 1.0f, "auto"),
    Q144P("144p (Ultra Saver)", "144p", 0.5f, "144p"),
    Q240P("240p (Data Saver)", "240p", 0.9f, "240p"),
    Q360P("360p (Balanced)", "360p", 2.2f, "360p"),
    Q480P("480p (Standard)", "480p", 4.0f, "480p"),
    Q720P("720p (HD)", "720p", 8.5f, "720p");

    companion object {
        fun fromTag(tag: String): VideoQuality {
            return entries.find { it.tag.equals(tag, ignoreCase = true) } ?: AUTO
        }
    }
}

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val channelName: String,
    val channelAvatarUrl: String,
    val subscriberCount: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val viewsCount: Long,
    val uploadedTimeAgo: String,
    val category: String,
    val stream144p: String = videoUrl,
    val stream240p: String = videoUrl,
    val stream360p: String = videoUrl,
    val stream720p: String = videoUrl,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val likesCount: Long = 12400L,
    val commentsCount: Int = 348,
    val isSubscribed: Boolean = false,
    val youtubeId: String? = null
) {
    val effectiveYouTubeId: String?
        get() {
            if (!youtubeId.isNullOrBlank()) return youtubeId
            if (id.startsWith("yt_")) return id.removePrefix("yt_")
            if (id.length == 11 && id.matches(Regex("[a-zA-Z0-9_-]{11}"))) return id
            return null
        }
    fun getStreamForQuality(quality: VideoQuality, dataSaverEnabled: Boolean): String {
        if (dataSaverEnabled && quality == VideoQuality.AUTO) {
            return stream240p
        }
        return when (quality) {
            VideoQuality.AUTO -> stream360p
            VideoQuality.Q144P -> stream144p
            VideoQuality.Q240P -> stream240p
            VideoQuality.Q360P -> stream360p
            VideoQuality.Q480P -> stream360p
            VideoQuality.Q720P -> stream720p
        }
    }

    fun getFormattedDuration(): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun getFormattedViews(): String {
        return when {
            viewsCount >= 1_000_000 -> String.format("%.1fM views", viewsCount / 1_000_000.0)
            viewsCount >= 1_000 -> String.format("%.1fK views", viewsCount / 1_000.0)
            else -> "$viewsCount views"
        }
    }
}
