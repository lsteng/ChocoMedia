package reson.chocomedia.util

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.*
import com.facebook.drawee.view.DraweeTransition.createTransitionSet
import com.facebook.drawee.view.SimpleDraweeView
import reson.chocomedia.R


class FragTransUtil {
    companion object {
        fun replaceContentFrag(frag: Fragment, fragName: String, fragmentManager: FragmentManager, imageView: ImageView, transitionName: String){
            val fragTransaction = fragmentManager.beginTransaction()
            fragTransaction
                .replace(R.id.container, frag, fragName)
                .addToBackStack(fragName)
                .addSharedElement(imageView, transitionName)
                .commit()
        }

        fun replaceContentFrag(frag: Fragment, fragName: String, fragmentManager: FragmentManager){
            val fragTransaction = fragmentManager.beginTransaction()
            fragTransaction
                .replace(R.id.container, frag, fragName)
                .addToBackStack(fragName)
                .commit()
        }

        fun setContentFrag(frag: Fragment, fragName: String, fragmentManager: FragmentManager){
            val fragTransaction = fragmentManager.beginTransaction()
            fragTransaction.replace(R.id.container, frag, fragName).commit()
        }

        fun switchContentFrag(from: Fragment?, to: Fragment, fragmentManager: FragmentManager) {
            if (from !== to) {
                val fragTransaction = fragmentManager.beginTransaction()
                //此处必须要进行判断，因为同一个fragment只能被add一次，否则会发生异常
                if (!to.isAdded) { //未添加
                    fragTransaction.hide(from!!)
                    fragTransaction.add(R.id.container, to)
                    fragTransaction.show(to).commit()
                } else {
                    fragTransaction.hide(from!!)
                    fragTransaction.show(to).commit()
                }
            }
        }
    }
}