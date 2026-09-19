package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CommentEntity
import com.example.model.DataSaverSettingsEntity
import com.example.model.DownloadedVideoEntity
import com.example.model.SubscriptionEntity
import com.example.model.UserInteractionEntity

@Database(
    entities = [
        UserInteractionEntity::class,
        CommentEntity::class,
        SubscriptionEntity::class,
        DownloadedVideoEntity::class,
        DataSaverSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TubeLiteDatabase : RoomDatabase() {
    abstract fun userInteractionDao(): UserInteractionDao
    abstract fun commentDao(): CommentDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun downloadDao(): DownloadDao
    abstract fun dataSaverDao(): DataSaverDao

    companion object {
        @Volatile
        private var INSTANCE: TubeLiteDatabase? = null

        fun getInstance(context: Context): TubeLiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TubeLiteDatabase::class.java,
                    "tubelite_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
