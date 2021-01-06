package com.pdf.converter.activity

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.pdf.converter.R
import com.pdf.converter.aide.Constants
import com.pdf.converter.manager.InitTBSManager
import com.pdf.converter.manager.SPManager
import com.pdf.converter.utils.Utils

class StartActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_start
    private var network: FrameLayout? = null
    private var networkOk: TextView? = null

    override fun init() {
        network = findViewById(R.id.network)
        findViewById<TextView>(R.id.network_ok).setOnClickListener { finish() }
        if (!Utils.isNetworkAvailable(this)){
            network?.visibility = View.VISIBLE
            return
        }
        //InitTBSManager.init(this)
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