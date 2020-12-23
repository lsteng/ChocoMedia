package reson.chocomedia

import androidx.multidex.MultiDexApplication

class MainApplication : MultiDexApplication() {
    companion object {
        lateinit var instance: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}