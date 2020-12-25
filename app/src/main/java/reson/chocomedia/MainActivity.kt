package reson.chocomedia

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import reson.chocomedia.constant.GlobalConstant
import reson.chocomedia.database.VideoBean
import reson.chocomedia.database.VideoDatabase
import reson.chocomedia.util.HttpUtil
import reson.chocomedia.database.VideoListBean
import reson.chocomedia.view.VideoListRecyclerAdapter
import kotlin.coroutines.CoroutineContext

class MainActivity: AppCompatActivity(), CoroutineScope {
    val TAG = "MainActivity"
    lateinit var mActivity: Activity
    var videoListRecyclerAdapter: VideoListRecyclerAdapter? = null

    lateinit var job: Job
    //此errorHandler用來接 CoroutineScope 沒有被 try-catch 包起來的 exceptions
    val errorHandler = CoroutineExceptionHandler { _, error ->
        Log.e(TAG, error.toString())
        this.runOnUiThread{
            if (progressBar != null){
                progressBar.visibility = View.GONE
            }
            AlertDialog.Builder(this).setMessage(error.message).setPositiveButton("OK", null).show()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO + errorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mActivity = this
        recycler.layoutManager = LinearLayoutManager(mActivity)
        recycler.setHasFixedSize(true)
        recycler.adapter = videoListRecyclerAdapter

        val gson = Gson()
        job = Job()
        launch {
            val response = HttpUtil.getDataSting(GlobalConstant.ApiUrl)
            val videoList = gson.fromJson(response, VideoListBean::class.java)
            VideoDatabase.getInstance(mActivity)?.videoInfoDao()?.insertAll(videoList.data)
            showResult(videoList.data)
        }

        search.setOnClickListener {
            launch {
                val keyWord = searchTV.text.toString().trim()
                var videoInfoList = VideoDatabase.getInstance(mActivity)?.videoInfoDao()?.searchVideoByName(keyWord)
                showResult(videoInfoList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun showResult(videoInfoList: List<VideoBean>?){
        mActivity.runOnUiThread{
            videoInfoList.let {
                if (it!=null && it.isNotEmpty()) {
                    videoListRecyclerAdapter = VideoListRecyclerAdapter(it, mActivity)
                    recycler.adapter = videoListRecyclerAdapter
                }
            }
            if (progressBar != null){
                progressBar.visibility = View.GONE
            }
        }
    }
}