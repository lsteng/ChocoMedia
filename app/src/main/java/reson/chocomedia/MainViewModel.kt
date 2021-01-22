package reson.chocomedia

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import reson.chocomedia.constant.GlobalConstant
import reson.chocomedia.database.SearchRecord
import reson.chocomedia.database.VideoBean
import reson.chocomedia.database.VideoDatabase
import reson.chocomedia.database.VideoListBean
import reson.chocomedia.util.HttpUtil

class MainViewModel(application: Application): AndroidViewModel(application) {
    val TAG = "MainViewModel"
    val context = getApplication<Application>().applicationContext
    val SearchKeyTag = "searchKey"
    val SearchDataTag = "searchData"

    val isShowProgress = MutableLiveData<Boolean>()
    val alertMessage = MutableLiveData<String>()
    val searchRecordAdapter = MutableLiveData<ArrayAdapter<String>>()
    val videoInfoResultList = MutableLiveData<List<VideoBean>>()
    val searchText = MutableLiveData<String>()
    val isHideKeyboard = MutableLiveData<Boolean>()

    //此errorHandler用來接 CoroutineScope 沒有被 try-catch 包起來的 exceptions
    val errorHandler = CoroutineExceptionHandler { _, error ->
        Log.e(TAG, error.toString())
        isShowProgress.value = false
    }
    val coroutineContext = Dispatchers.IO + errorHandler

    init {
        isShowProgress.value = true
        getSearchRecord()
        val searchDataMap = getSearchData()
        val searchKey = searchDataMap?.get(SearchKeyTag)
        val searchData = searchDataMap?.get(SearchDataTag)
        if (searchData.isNullOrEmpty()){
            getData(true)
        } else{
            getData(false)
            if(!searchKey.isNullOrEmpty()){
                searchText.value = searchKey ?: ""
                isHideKeyboard.value = true
            }
            val listType = object : TypeToken<ArrayList<VideoBean>>() {}.type
            videoInfoResultList.value = Gson().fromJson(searchData, listType)
        }
    }

    fun getSearchRecord(){
        viewModelScope.launch(coroutineContext) {
            val searchRecordList = VideoDatabase.getInstance(context)?.SearchRecordDao()?.queryLimit(5)
            searchRecordList.let {
                searchRecordAdapter.postValue(ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, it))
            }
        }
    }

    fun putSearchData(key: String, data: String){
        val prefs = context.getSharedPreferences("searchInfo", Context.MODE_PRIVATE)
        prefs.edit().putString(SearchKeyTag, key).commit()
        prefs.edit().putString(SearchDataTag, data).commit()
    }

    fun getSearchData(): Map<String?, String?>?{
        val prefs = context.getSharedPreferences("searchInfo", Context.MODE_PRIVATE)
        var dataMap = mutableMapOf<String?, String?>()
        dataMap.put(SearchKeyTag, prefs.getString(SearchKeyTag, null))
        dataMap.put(SearchDataTag, prefs.getString(SearchDataTag, null))
        return dataMap
    }

    fun getData(isShowResult: Boolean){
        if (HttpUtil.haveInternet(context)){
            alertMessage.value = ""
            if (isShowResult){
                isShowProgress.value = true
            }
            viewModelScope.launch(coroutineContext) {
                val response = HttpUtil.getDataSting(GlobalConstant.ApiUrl)
                val videoList = Gson().fromJson(response, VideoListBean::class.java)
                VideoDatabase.getInstance(context)?.videoInfoDao()?.insertAll(videoList.data)
                if (isShowResult){
                    videoInfoResultList.postValue(videoList.data)
                }
            }
        } else{
            if (isShowResult){
                val alertString = "No Internet Connection"
                isShowProgress.value = false
                alertMessage.value = alertString
            }
        }
    }

    fun queryData(keyword: String, isSaveKeyword: Boolean) {
        isShowProgress.value = true
        viewModelScope.launch(coroutineContext) {
            var videoInfoList = VideoDatabase.getInstance(context)?.videoInfoDao()?.searchVideoByName(keyword)
            isShowProgress.postValue(false)
            if (videoInfoList.isNullOrEmpty()) {
                alertMessage.postValue("查無相關戲劇資料！")
            } else {
                if (isSaveKeyword) {
                    putSearchData(keyword, Gson().toJson(videoInfoList))
                    VideoDatabase.getInstance(context)?.SearchRecordDao()?.insert(SearchRecord(keyword, System.currentTimeMillis()))
                    getSearchRecord()
                }
                videoInfoResultList.postValue(videoInfoList ?: arrayListOf<VideoBean>())
            }
        }
    }
}