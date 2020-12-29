package reson.chocomedia.frag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_videoinfo.view.*
import reson.chocomedia.MainApplication
import reson.chocomedia.R

private const val ARG_thumb = "param1"
private const val ARG_name = "param2"
private const val ARG_rating = "param3"
private const val ARG_created_at = "param4"
private const val ARG_total_views = "param5"

class VideoInfoFrag: Fragment() {
    val TAG = "VideoInfoFrag"
    lateinit var mContext: Context
    var thumb: String = ""
    var name: String = ""
    var rating: String = ""
    var created_at: String = ""
    var total_views: String = ""

    companion object {
        val FRAG_TRANS_NAME = "VideoInfoFrag"

        @JvmStatic
        fun newInstance(thumb: String, name: String, rating: String, created_at: String, total_views: String) =
            VideoInfoFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_thumb, thumb)
                    putString(ARG_name, name)
                    putString(ARG_rating, rating)
                    putString(ARG_created_at, created_at)
                    putString(ARG_total_views, total_views)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            thumb = it.getString(ARG_thumb)
            name = it.getString(ARG_name)
            rating = it.getString(ARG_rating)
            created_at = it.getString(ARG_created_at)
            total_views = it.getString(ARG_total_views)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_videoinfo, container, false)
        view.ratingTV.text = rating
        view.nameTV.text = name
        view.totalviewsTV.text = total_views
        view.createdTV.text = created_at
        MainApplication.imageLoader.displayImage(thumb, view.thumbIV)
//        view.thumbIV.setImageURI(thumb)
        return view
    }

}