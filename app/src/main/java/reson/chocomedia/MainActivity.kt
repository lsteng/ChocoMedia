package reson.chocomedia

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_refresh.*
import reson.chocomedia.database.VideoBean
import reson.chocomedia.view.VideoListRecyclerAdapter

class MainActivity: AppCompatActivity() {
    val TAG = "MainActivity"
    lateinit var mActivity: Activity
    var videoListRecyclerAdapter: VideoListRecyclerAdapter? = null
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mActivity = this

        recycler.layoutManager = LinearLayoutManager(mActivity)
        recycler.setHasFixedSize(true)
        recycler.adapter = videoListRecyclerAdapter

        viewModel.isShowProgress.observe(this, Observer {
            showProgress(it)
        })
        viewModel.alertMessage.observe(this, Observer {
            showProgress(false)
            if(!it.isNullOrEmpty()){
                if (videoListRecyclerAdapter != null){
                    Snackbar.make(mainRL, it, Snackbar.LENGTH_SHORT).show()
                } else{
                    showRetry(true, it)
                }
            }
        })
        viewModel.searchRecordAdapter.observe(this, Observer {
            searchTV.setAdapter(it)
        })
        viewModel.videoInfoResultList.observe(this, Observer {
            showResult(it)
        })
        viewModel.searchText.observe(this, Observer {
            searchTV.setText(it)
        })
        viewModel.isHideKeyboard.observe(this, Observer {
            if(it){
                hideKeyboard()
            }
        })

        refreshRL.setOnClickListener {
            viewModel.getData(true)
        }

        swipeRefreshLayout.setOnRefreshListener {
            searchTV.setText("")
            hideKeyboard()
            viewModel.putSearchData("", "")
            viewModel.getData(true)
        }

        search.setOnClickListener {
            clickSearchBtn()
        }

        searchTV.setOnEditorActionListener (
            OnEditorActionListener { textView, actionId, keyEvent  ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    clickSearchBtn()
                    return@OnEditorActionListener true
                }
                false
            }
        )
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    fun clickSearchBtn(){
        val keyword = searchTV.text.toString().trim()
        if(!keyword.isNullOrEmpty()){
            hideKeyboard()
            viewModel.queryData(searchTV.text.toString().trim(), true)
        }
    }

    fun showResult(videoInfoList: List<VideoBean>?){
        mActivity.runOnUiThread{
            videoInfoList.let {
                if (!it.isNullOrEmpty()) {
                    videoListRecyclerAdapter = VideoListRecyclerAdapter(it, mActivity, supportFragmentManager)
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
        if (isShow){
//            if (progressBar != null){ progressBar.visibility = View.VISIBLE }
            swipeRefreshLayout.setRefreshing(true)
        } else{
//            if (progressBar != null){ progressBar.visibility = View.GONE }
            swipeRefreshLayout.setRefreshing(false)
        }
    }

    fun hideKeyboard(){
        searchTV.clearFocus()
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}