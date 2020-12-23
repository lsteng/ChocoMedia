package reson.chocomedia.util

class VideoListBean(
    val data: List<VideoBean>
)

class VideoBean(
    val dramaId: Int,
    val name: String,       //戲劇名稱
    val totalViews: Long,   //觀看次數
    val createdAt: String,  //出版日期
    val thumb: String,      //戲劇縮圖
    val rating: Double      //戲劇評分
)