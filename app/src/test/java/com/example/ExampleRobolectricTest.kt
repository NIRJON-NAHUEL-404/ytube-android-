package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.SampleVideoCatalog
import com.example.data.TubeLiteDatabase
import com.example.model.CommentEntity
import com.example.model.DataSaverSettingsEntity
import com.example.model.DownloadedVideoEntity
import com.example.model.SubscriptionEntity
import com.example.model.UserInteractionEntity
import com.example.model.VideoQuality
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: TubeLiteDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TubeLiteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("TubeLite", appName)
    }

    @Test
    fun `sample video catalog contains data-saver optimized videos`() {
        val videos = SampleVideoCatalog.sampleVideos
        assertTrue("Video catalog should not be empty", videos.isNotEmpty())
        val firstVideo = videos.first()
        assertNotNull("First video should have title", firstVideo.title)
        assertNotNull("First video should have stream URL", firstVideo.videoUrl)
        assertTrue("Duration should be positive", firstVideo.durationSeconds > 0)

        // Verify data saver URL selection
        val normalUrl = firstVideo.getStreamForQuality(VideoQuality.Q720P, dataSaverEnabled = false)
        val saverUrl = firstVideo.getStreamForQuality(VideoQuality.AUTO, dataSaverEnabled = true)
        assertNotNull(normalUrl)
        assertNotNull(saverUrl)
    }

    @Test
    fun `database handles user interactions comments subscriptions downloads and data saver`() = runBlocking {
        val interactionDao = database.userInteractionDao()
        val commentDao = database.commentDao()
        val subDao = database.subscriptionDao()
        val downloadDao = database.downloadDao()
        val dataSaverDao = database.dataSaverDao()

        val testVideo = SampleVideoCatalog.sampleVideos.first()

        // Test Like & Dislike
        interactionDao.insertOrUpdate(
            UserInteractionEntity(
                videoId = testVideo.id,
                isLiked = true,
                likeCountOffset = 1
            )
        )
        val interaction = interactionDao.getInteraction(testVideo.id).first()
        assertNotNull(interaction)
        assertTrue("Video should be liked", interaction?.isLiked == true)

        // Test Add Comment
        val comment = CommentEntity(
            id = "c1",
            videoId = testVideo.id,
            authorName = "টেস্ট ইউজার",
            authorAvatarUrl = "avatar.jpg",
            commentText = "অসাধারণ ভিডিও! অনেক কিছু শিখলাম।",
            timestamp = System.currentTimeMillis()
        )
        commentDao.insertComment(comment)
        val comments = commentDao.getCommentsForVideo(testVideo.id).first()
        assertTrue("Comment should be added", comments.any { it.commentText.contains("অসাধারণ") })

        // Test Subscription
        subDao.insertOrUpdate(
            SubscriptionEntity(
                channelName = "Tech Bangla & Code",
                channelAvatarUrl = "avatar.jpg",
                subscriberCount = "1.25M",
                isSubscribed = true
            )
        )
        val subs = subDao.getAllSubscriptions().first()
        assertTrue("Should be subscribed to channel", subs.any { it.channelName == "Tech Bangla & Code" })

        // Test Download
        val dl = DownloadedVideoEntity(
            id = "dl_1",
            videoId = testVideo.id,
            title = testVideo.title,
            channelName = testVideo.channelName,
            channelAvatarUrl = testVideo.channelAvatarUrl,
            thumbnailUrl = testVideo.thumbnailUrl,
            videoUrl = testVideo.videoUrl,
            localFilePath = "/data/video.mp4",
            quality = "240p",
            fileSizeBytes = 4500000L,
            durationSeconds = testVideo.durationSeconds,
            progress = 100,
            isCompleted = true
        )
        downloadDao.insertOrUpdate(dl)
        val download = downloadDao.getDownload(testVideo.id).first()
        assertNotNull("Download should be recorded", download)
        assertEquals(testVideo.title, download?.title)

        // Test Data Saver settings
        dataSaverDao.insertOrUpdate(
            DataSaverSettingsEntity(
                id = 1,
                dataSaverEnabled = true,
                dailyBudgetMb = 150,
                usedMbToday = 14.5f
            )
        )
        dataSaverDao.addUsedData(2.5f)
        val settings = dataSaverDao.getSettings().first()
        assertNotNull(settings)
        assertEquals(17.0f, settings!!.usedMbToday, 0.01f)
    }
}
