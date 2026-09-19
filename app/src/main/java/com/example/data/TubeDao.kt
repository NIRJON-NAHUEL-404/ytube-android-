package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CommentEntity
import com.example.model.DataSaverSettingsEntity
import com.example.model.DownloadedVideoEntity
import com.example.model.SubscriptionEntity
import com.example.model.UserInteractionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInteractionDao {
    @Query("SELECT * FROM user_interactions WHERE videoId = :videoId")
    fun getInteraction(videoId: String): Flow<UserInteractionEntity?>

    @Query("SELECT * FROM user_interactions WHERE isWatchHistory = 1 ORDER BY watchTimestamp DESC")
    fun getWatchHistory(): Flow<List<UserInteractionEntity>>

    @Query("SELECT * FROM user_interactions WHERE isLiked = 1 ORDER BY watchTimestamp DESC")
    fun getLikedVideos(): Flow<List<UserInteractionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(interaction: UserInteractionEntity)

    @Query("UPDATE user_interactions SET lastWatchedPositionMs = :position, watchTimestamp = :timestamp, isWatchHistory = 1 WHERE videoId = :videoId")
    suspend fun updateWatchHistory(videoId: String, position: Long, timestamp: Long)

    @Query("DELETE FROM user_interactions WHERE isWatchHistory = 1")
    suspend fun clearWatchHistory()
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Update
    suspend fun updateComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY subscribedAt DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE channelName = :channelName")
    fun getSubscription(channelName: String): Flow<SubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE channelName = :channelName")
    suspend fun unsubscribe(channelName: String)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadedVideoEntity>>

    @Query("SELECT * FROM downloads WHERE videoId = :videoId")
    fun getDownload(videoId: String): Flow<DownloadedVideoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(download: DownloadedVideoEntity)

    @Query("UPDATE downloads SET progress = :progress, isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, isCompleted: Boolean)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: String)
}

@Dao
interface DataSaverDao {
    @Query("SELECT * FROM data_saver_settings WHERE id = 1")
    fun getSettings(): Flow<DataSaverSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: DataSaverSettingsEntity)

    @Query("UPDATE data_saver_settings SET usedMbToday = usedMbToday + :mbDelta WHERE id = 1")
    suspend fun addUsedData(mbDelta: Float)

    @Query("UPDATE data_saver_settings SET dailyBudgetMb = :budget WHERE id = 1")
    suspend fun updateDailyBudget(budget: Int)

    @Query("UPDATE data_saver_settings SET dataSaverEnabled = :enabled WHERE id = 1")
    suspend fun setDataSaverEnabled(enabled: Boolean)

    @Query("UPDATE data_saver_settings SET defaultQuality = :quality WHERE id = 1")
    suspend fun setDefaultQuality(quality: String)
}
