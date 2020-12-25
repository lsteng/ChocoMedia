package reson.chocomedia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(VideoBean::class), version = 1)
abstract class VideoDatabase: RoomDatabase() {
    abstract fun videoInfoDao(): VideoInfoDao

    //DB Singleton Instance
    companion object {
        private var instance: VideoDatabase? = null
        fun getInstance(context: Context): VideoDatabase? {
            if (instance == null) {
                instance = Room
                        .databaseBuilder(context, VideoDatabase::class.java, "room.db")
                        .build()
            }
            return instance
        }
    }
}