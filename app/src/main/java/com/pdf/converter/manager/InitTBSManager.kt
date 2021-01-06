package com.pdf.converter.manager

import android.content.Context
import android.util.Log
import com.pdf.converter.aide.Constants.INIT_TBS
import com.tencent.smtt.sdk.QbSdk

/**
 * @author : ydli
 * @time : 2020/12/28 11:02
 * @description :
 */
object InitTBSManager {

    fun init(context: Context) {
        QbSdk.initX5Environment(context, cb)
        QbSdk.setDownloadWithoutWifi(true)
    }

    private val cb: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
        override fun onCoreInitFinished() {
            Log.d("initTBS", "TBS init finish")
        }

        override fun onViewInitFinished(init: Boolean) {
            //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核
            SPManager.init().setBoolean(INIT_TBS, init)
            Log.d("initTBS", "onViewInitFinished: $init")
        }
    }
}