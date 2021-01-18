package com.pdf.converter.activity

import android.app.Activity
import com.pdf.converter.R
import com.pdf.converter.aide.Constants
import com.pdf.converter.manager.SPManager

class StartActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_start

    override fun init() {
        SPManager.init().setBoolean(Constants.INIT_TBS, true)
        window.decorView.postDelayed({
            newStart(this)
        }, 2000)
    }

    companion object {

        fun newStart(activity: Activity) {
            MainActivity.newStart(activity)
            activity.finish()
        }
    }
}