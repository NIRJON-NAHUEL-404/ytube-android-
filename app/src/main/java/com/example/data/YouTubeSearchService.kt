package com.example.data

import android.util.Log
import com.example.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object YouTubeSearchService {
    private const val TAG = "YouTubeSearchService"

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val ytDataPattern = Pattern.compile("var ytInitialData = (\\{.*?\\});</script>")

    suspend fun searchVideos(query: String): List<Video> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext emptyList()

        try {
            val encoded = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://www.youtube.com/results?search_query=$encoded"

            val request = Request.Builder()
                .url(url)
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
                )
                .header("Accept-Language", "en-US,en;q=0.9,bn;q=0.8")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "YouTube search HTTP failed: ${response.code}")
                return@withContext emptyList()
            }

            val html = response.body?.string() ?: return@withContext emptyList()
            val matcher = ytDataPattern.matcher(html)
            if (!matcher.find()) {
                Log.w(TAG, "Could not find ytInitialData in response")
                return@withContext emptyList()
            }

            val jsonStr = matcher.group(1) ?: return@withContext emptyList()
            val root = JSONObject(jsonStr)

            val results = mutableListOf<Video>()

            val contents = root.optJSONObject("contents")
                ?.optJSONObject("twoColumnSearchResultsRenderer")
                ?.optJSONObject("primaryContents")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents") ?: return@withContext emptyList()

            for (i in 0 until contents.length()) {
                val section = contents.optJSONObject(i) ?: continue
                val itemSectionContents = section.optJSONObject("itemSectionRenderer")
                    ?.optJSONArray("contents") ?: continue

                for (j in 0 until itemSectionContents.length()) {
                    val item = itemSectionContents.optJSONObject(j) ?: continue
                    val vr = item.optJSONObject("videoRenderer") ?: continue

                    val videoId = vr.optString("videoId")
                    if (videoId.isNullOrBlank()) continue

                    val title = vr.optJSONObject("title")
                        ?.optJSONArray("runs")
                        ?.optJSONObject(0)
                        ?.optString("text")
                        ?: vr.optJSONObject("title")?.optString("simpleText")
                        ?: "YouTube Video"

                    val channelName = vr.optJSONObject("ownerText")
                        ?.optJSONArray("runs")
                        ?.optJSONObject(0)
                        ?.optString("text")
                        ?: "Creator"

                    val thumbArray = vr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                    val thumbUrl = if (thumbArray != null && thumbArray.length() > 0) {
                        thumbArray.optJSONObject(thumbArray.length() - 1)?.optString("url")
                            ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
                    } else {
                        "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
                    }

                    val lengthText = vr.optJSONObject("lengthText")?.optString("simpleText") ?: ""
                    val durationSec = parseDurationToSeconds(lengthText)

                    val viewsText = vr.optJSONObject("viewCountText")?.optString("simpleText") ?: ""
                    val viewsCount = parseViewsCount(viewsText)

                    val publishedAgo = vr.optJSONObject("publishedTimeText")?.optString("simpleText") ?: "Recently"

                    val category = determineCategory(title, trimmed)

                    results.add(
                        Video(
                            id = "yt_$videoId",
                            title = title,
                            description = "YouTube Official Video • $channelName • $viewsText",
                            channelName = channelName,
                            channelAvatarUrl = "https://picsum.photos/seed/$channelName/200/200",
                            subscriberCount = "1.5M",
                            videoUrl = "https://www.youtube.com/watch?v=$videoId",
                            thumbnailUrl = thumbUrl,
                            durationSeconds = durationSec,
                            viewsCount = viewsCount,
                            uploadedTimeAgo = publishedAgo,
                            category = category,
                            youtubeId = videoId,
                            likesCount = (viewsCount / 20).coerceAtLeast(1200L),
                            commentsCount = (viewsCount / 200).toInt().coerceIn(45, 5000)
                        )
                    )
                }
            }

            Log.d(TAG, "Successfully parsed ${results.size} real YouTube videos for query: '$query'")
            results
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching YouTube videos for '$query'", e)
            emptyList()
        }
    }

    private fun parseDurationToSeconds(text: String): Long {
        if (text.isBlank()) return 240L
        val parts = text.trim().split(":")
        var sec = 0L
        for (p in parts) {
            sec = sec * 60 + (p.toLongOrNull() ?: 0L)
        }
        return if (sec > 0) sec else 240L
    }

    private fun parseViewsCount(text: String): Long {
        val clean = text.replace(",", "").replace(".", "")
        val digits = Regex("\\d+").find(clean)?.value?.toLongOrNull() ?: 50000L
        return when {
            text.contains("M", ignoreCase = true) -> (digits * 1000000L).coerceAtMost(2000000000L)
            text.contains("K", ignoreCase = true) -> digits * 1000L
            text.contains("B", ignoreCase = true) -> (digits * 1000000000L)
            else -> digits.coerceAtLeast(5000L)
        }
    }

    private fun determineCategory(title: String, query: String): String {
        val lower = "$title $query".lowercase()
        return when {
            lower.contains("গান") || lower.contains("song") || lower.contains("music") ||
                    lower.contains("গজল") || lower.contains("নাশীদ") || lower.contains("সুর") -> "Music"
            lower.contains("tech") || lower.contains("মোবাইল") || lower.contains("android") -> "Tech"
            lower.contains("travel") || lower.contains("ভ্রমণ") || lower.contains("ট্যুর") -> "Travel"
            lower.contains("gaming") || lower.contains("গেম") -> "Gaming"
            lower.contains("রেসিপি") || lower.contains("food") || lower.contains("রান্না") -> "Food"
            lower.contains("animation") || lower.contains("কার্টুন") -> "Animation"
            else -> "YouTube"
        }
    }
}
