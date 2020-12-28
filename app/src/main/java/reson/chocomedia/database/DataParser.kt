package reson.chocomedia.database

import androidx.room.Entity
import androidx.room.PrimaryKey

class VideoListBean(
    val data: List<VideoBean>
)

@Entity
class VideoBean(
    @PrimaryKey
    val drama_id: Int,
    val name: String,         //戲劇名稱
    val total_views: String,  //觀看次數
    val created_at: String,   //出版日期
    val thumb: String,        //戲劇縮圖
    val rating: Double        //戲劇評分
)

@Entity
class SearchRecord(
        @PrimaryKey
        val keyword: String,
        val time: Long
)