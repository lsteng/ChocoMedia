package reson.chocomedia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(VideoBean::class, SearchRecord::class), version = 2)
abstract class VideoDatabase: RoomDatabase() {
    abstract fun videoInfoDao(): VideoInfoDao
    abstract fun SearchRecordDao(): SearchRecordDao

    //DB Singleton Instance
    companion object {
        private var instance: VideoDatabase? = null
        fun getInstance(context: Context): VideoDatabase? {
            if (instance == null) {
                instance = Room
                        .databaseBuilder(context, VideoDatabase::class.java, "room.db")
                        .addMigrations(MIGRATION_1_2)
                        .build()
            }
            return instance
        }

        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `SearchRecord` (`keyword` TEXT, `time` INTEGER, PRIMARY KEY(`keyword`))")
            }
        }
    }
}