package com.pdf.converter.activity

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.donkingliang.imageselector.utils.ImageUtil
import com.pdf.converter.aide.FirebaseTracker

/**
 * @author : ydli
 * @time : 2020/12/22 18:57
 * @description :
 */
abstract class BaseActivity : AppCompatActivity() {
    private var mReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        ImageUtil.changStatusIconColor(this@BaseActivity, true)
        init()
        initData()
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun init()

    open fun initData() {}

    fun track(string: String?) = FirebaseTracker.instance.track(string)

    protected open fun registerBroadcastReceiver(receiver: BroadcastReceiver) {
        mReceiver = receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("lib_click")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver!!)
        }
        super.onDestroy()
    }
}