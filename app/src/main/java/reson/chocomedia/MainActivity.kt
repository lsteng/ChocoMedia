package reson.chocomedia

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import reson.chocomedia.constant.GlobalConstant
import reson.chocomedia.util.HttpUtil
import reson.chocomedia.util.VideoBean
import reson.chocomedia.util.VideoListBean
import kotlin.coroutines.CoroutineContext

class MainActivity: AppCompatActivity(), CoroutineScope {
    val TAG = "MainActivity"

    lateinit var mActivity: Activity
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

        val gson = Gson()
        job = Job()
        launch {
            val response = HttpUtil.getDataSting(GlobalConstant.ApiUrl)
            val videoList = gson.fromJson(response, VideoListBean::class.java)
            mActivity.runOnUiThread{
                if (videoList?.data != null) {
                    for (video in videoList?.data){
                        logTV.append("${video.name} \n")
                    }
                }
                logTV.append(videoList?.data?.get(0)?.name)
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}