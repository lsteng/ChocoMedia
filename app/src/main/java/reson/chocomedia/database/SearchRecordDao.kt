package reson.chocomedia.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchRecord: SearchRecord)

    @Query("SELECT keyword FROM SearchRecord ORDER BY time DESC")
    fun queryAll(): List<String>
}