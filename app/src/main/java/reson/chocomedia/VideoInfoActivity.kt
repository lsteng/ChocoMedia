package reson.chocomedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_videoinfo.*

class VideoInfoActivity: AppCompatActivity() {
    val TAG = "VideoInfoActivity"

    companion object {
        val Key_thumb = "param1"
        val Key_name = "param2"
        val Key_rating = "param3"
        val Key_created_at = "param4"
        val Key_total_views = "param5"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoinfo)

        ratingTV.text = intent.getStringExtra(Key_rating)
        nameTV.text = intent.getStringExtra(Key_name)
        totalviewsTV.text = "觀看:${intent.getStringExtra(Key_total_views)}"
        createdTV.text = intent.getStringExtra(Key_created_at)
        MainApplication.imageLoader.displayImage(intent.getStringExtra(Key_thumb), thumbIV)
    }
}