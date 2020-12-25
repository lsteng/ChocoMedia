package reson.chocomedia

import android.graphics.Bitmap
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType


class MainApplication : MultiDexApplication() {
    companion object {
        lateinit var imageLoader: ImageLoader
    }

    override fun onCreate() {
        super.onCreate()
        initialImageLoader()
        initStetho()
    }

    fun initialImageLoader() {
        val options = DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.loading)
            .showImageForEmptyUri(R.mipmap.loading)
            .showImageOnFail(R.mipmap.loading)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build()
        val config = ImageLoaderConfiguration.Builder(applicationContext)
                //.memoryCacheExtraOptions(720, 1280)
                //.memoryCache(LruMemoryCache(2 * 512 * 512))
                //.diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .defaultDisplayImageOptions(options)
                .build()
        imageLoader = ImageLoader.getInstance()
        imageLoader.init(config)
    }

    //資料庫內容可用Chrome看
    //inspect(chrome://inspect/#devices)
    fun initStetho() {
        Stetho.initialize(Stetho
                .newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build())
    }
}