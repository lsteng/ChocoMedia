package reson.chocomedia

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_refresh.*
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
            showProgress(false)
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

        job = Job()
        initData()
        refreshRL.setOnClickListener {
            getData(true)
        }

        search.setOnClickListener {
            queryData(searchTV.text.toString().trim(), true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun initData(){
        showProgress(true)
        val keyword = getSearchKey()
        if (keyword.isNullOrEmpty()){
            getData(true)
        } else{
            queryData(keyword, false)
            getData(false)
        }
    }

    fun getData(isShowResult: Boolean){
        if (HttpUtil.haveInternet(mActivity)){
            showRetry(false, "")
            if (isShowResult){
                showProgress(true)
            }
            val gson = Gson()
            launch {
                val response = HttpUtil.getDataSting(GlobalConstant.ApiUrl)
                val videoList = gson.fromJson(response, VideoListBean::class.java)
                VideoDatabase.getInstance(mActivity)?.videoInfoDao()?.insertAll(videoList.data)
                if (isShowResult){
                    showResult(videoList.data)
                }
            }
        } else{
            if (isShowResult){
                showRetry(true, "No Internet Connection")
            }
        }
    }

    fun putSearchKey(value: String){
        val prefs = getSharedPreferences("searchInfo", Context.MODE_PRIVATE)
        prefs.edit().putString("searchKey", value).commit()
    }

    fun getSearchKey(): String?{
        val prefs = getSharedPreferences("searchInfo", Context.MODE_PRIVATE)
        return prefs.getString("searchKey", null)
    }

    fun queryData(keyword: String, isSaveKeyword: Boolean){
        showProgress(true)
        launch {
            var videoInfoList = VideoDatabase.getInstance(mActivity)?.videoInfoDao()?.searchVideoByName(keyword)
            if (videoInfoList.isNullOrEmpty()){
                Snackbar.make(mainRL, "查無相關戲劇資料！", Snackbar.LENGTH_SHORT).show()
            } else{
                if (isSaveKeyword){
                    putSearchKey(keyword)
                }
                showResult(videoInfoList)
            }
        }
    }

    fun showResult(videoInfoList: List<VideoBean>?){
        mActivity.runOnUiThread{
            videoInfoList.let {
                if (!it.isNullOrEmpty()) {
                    videoListRecyclerAdapter = VideoListRecyclerAdapter(it, mActivity)
                    recycler.adapter = videoListRecyclerAdapter
                }
            }
            showProgress(false)
            showRetry(false, "")
        }
    }

    fun showRetry(isShow:Boolean, alert: String){
        if (isShow){
            refreshRL.visibility = View.VISIBLE
            alertTV.text = alert
            showProgress(false)
        } else{
            refreshRL.visibility = View.GONE
        }
    }

    fun showProgress(isShow: Boolean){
        if (progressBar != null){
            if (isShow){
                progressBar.visibility = View.VISIBLE
            } else{
                progressBar.visibility = View.GONE
            }
        }
    }


}