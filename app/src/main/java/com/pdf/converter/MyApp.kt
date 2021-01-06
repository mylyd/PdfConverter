package com.pdf.converter

import android.annotation.SuppressLint
import androidx.multidex.MultiDexApplication
import com.donkingliang.imageselector.toast.MToast
import com.pdf.converter.aide.FirebaseTracker
import com.pdf.converter.manager.FileManager
import com.pdf.converter.manager.InitTBSManager

/**
 * @author : ydli
 * @time : 2020/12/22 18:28
 * @description :
 */
class MyApp : MultiDexApplication() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseTracker.instance.init(this)
        MToast.instant().init(this)
    }
}