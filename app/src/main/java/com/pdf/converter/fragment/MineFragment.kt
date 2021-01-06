package com.pdf.converter.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pdf.converter.R
import com.pdf.converter.activity.MineFAQActivity
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.manager.ShareManager
import com.pdf.converter.utils.Utils

/**
 * @author : ydli
 * @time : 2020/12/23 9.28
 * @description : 我的界面
 */
class MineFragment : BaseFragment(), View.OnClickListener {
    override fun getLayoutId(): Int = R.layout.fragment_mine
    private var faqLayout: View? = null
    private var privacyLayout: View? = null
    private var feedbackLayout: View? = null
    private var shareLayout: View? = null
    private var version: TextView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        findViewById<TextView>(R.id.version)?.text = "v${Utils.getVersionName(context!!)}"
        faqLayout = findViewById(R.id.mine_faq)
        privacyLayout = findViewById(R.id.mine_privacy)
        feedbackLayout = findViewById(R.id.mine_feedback)
        shareLayout = findViewById(R.id.mine_share)
        faqLayout?.setOnClickListener(this)
        privacyLayout?.setOnClickListener(this)
        feedbackLayout?.setOnClickListener(this)
        shareLayout?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            faqLayout -> {
                track(MyTrack.mine_faq_click)
                MineFAQActivity.newStart(context!!)
            }
            privacyLayout -> {
                track(MyTrack.mine_privacy_click)
                Utils.startWebView(context!!, resources.getString(R.string.privacy_url))
            }
            feedbackLayout -> {
                track(MyTrack.mine_feedback_click)
                Utils.startWebView(context!!, resources.getString(R.string.feed_back))
            }
            shareLayout -> {
                track(MyTrack.mine_shareapp_click)
                ShareManager.toSystemShare(
                    context!!,
                    resources.getString(R.string.share_app_content)
                )
            }
        }
    }
}