package reson.chocomedia.view

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_video.view.*
import reson.chocomedia.MainApplication
import reson.chocomedia.R
import reson.chocomedia.VideoInfoActivity
import reson.chocomedia.database.VideoBean
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*


class VideoListRecyclerAdapter(val dataList: List<VideoBean>, val activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        if (holder is VideoItemViewHolder){
            val videoInfo = dataList[pos]
            MainApplication.imageLoader.displayImage(videoInfo.thumb, holder.thumbIV)
            val rateString = "★ ${videoInfo.rating.roundTo2DecimalPlaces()}"
            holder.ratingTV.text = rateString
            holder.nameTV.text = videoInfo.name
            val dateString = convertDateString(videoInfo.created_at)
            holder.createdTV.text = dateString
            holder.itemll.setOnClickListener {
                val intent = Intent(activity, VideoInfoActivity::class.java).apply {
                    putExtra(VideoInfoActivity.Key_thumb, videoInfo.thumb)
                    putExtra(VideoInfoActivity.Key_name, videoInfo.name)
                    putExtra(VideoInfoActivity.Key_rating, rateString)
                    putExtra(VideoInfoActivity.Key_created_at, dateString)
                    putExtra(VideoInfoActivity.Key_total_views, videoInfo.total_views)
                }
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.thumbIV, activity.resources.getString(R.string.transition_name))
                startActivity(activity, intent, options.toBundle())
            }
        }
    }

    fun Double.roundTo2DecimalPlaces() = BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    fun convertDateString(utcTime: String): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val after: Date = sdf.parse(utcTime)
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss")
        sdf.timeZone = TimeZone.getDefault();
        return sdf.format(after)
    }
}

class VideoItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var itemll = view.itemll
    var thumbIV = view.thumbIV
    var ratingTV = view.ratingTV
    var nameTV = view.nameTV
    var createdTV = view.createdTV
}