package reson.chocomedia.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VideoInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(videoBean: VideoBean)

    @Query("SELECT * FROM VideoBean WHERE name LIKE :searchName")
    fun queryByName(searchName: String): List<VideoBean>

    fun searchVideoByName(keyWord: String): List<VideoBean>{
        return queryByName("%$keyWord%")
    }

    fun insertAll(videoDataList: List<VideoBean>){
        for (videoInfo in videoDataList){
            insert(videoInfo)
        }
    }
}