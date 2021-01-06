package com.pdf.converter.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import android.widget.ImageView
import com.donkingliang.imageselector.utils.ImageUtil
import com.pdf.converter.R

class MineFAQActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutId(): Int = R.layout.activity_mine_faq
    private var back: ImageView? = null
    private var faq1: ImageView? = null
    private var faq2: ImageView? = null
    private var faq3: ImageView? = null
    private var faqLayout1: View? = null
    private var faqLayout2: View? = null
    private var faqLayout3: View? = null
    private var faqText1: View? = null
    private var faqText2: View? = null
    private var faqText3: View? = null
    private var isFaq1: Boolean = false
    private var isFaq2: Boolean = false
    private var isFaq3: Boolean = false

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        back = findViewById(R.id.back)
        faq1 = findViewById(R.id.mine_faq_img_1)
        faq2 = findViewById(R.id.mine_faq_img_2)
        faq3 = findViewById(R.id.mine_faq_img_3)
        faqText1 = findViewById(R.id.faq_content_1)
        faqText2 = findViewById(R.id.faq_content_2)
        faqText3 = findViewById(R.id.faq_content_3)
        faqLayout1 = findViewById(R.id.faq_layout_1)
        faqLayout2 = findViewById(R.id.faq_layout_2)
        faqLayout3 = findViewById(R.id.faq_layout_3)
        back?.setOnClickListener(this)
        faqLayout1?.setOnClickListener(this)
        faqLayout2?.setOnClickListener(this)
        faqLayout3?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            back -> {
                onBackPressed()
            }
            faqLayout1 -> {
                if (isFaq1) {
                    faqText1?.visibility = GONE
                    faq1?.setImageResource(R.mipmap.mip_mine_faq_down)
                    isFaq1 = false
                } else {
                    faqText1?.visibility = VISIBLE
                    faq1?.setImageResource(R.mipmap.mip_mine_faq_up)
                    isFaq1 = true
                }
            }
            faqLayout2 -> {
                if (isFaq2) {
                    faqText2?.visibility = GONE
                    faq2?.setImageResource(R.mipmap.mip_mine_faq_down)
                    isFaq2 = false
                } else {
                    faqText2?.visibility = VISIBLE
                    faq2?.setImageResource(R.mipmap.mip_mine_faq_up)
                    isFaq2 = true
                }
            }
            faqLayout3 -> {
                if (isFaq3) {
                    faqText3?.visibility = GONE
                    faq3?.setImageResource(R.mipmap.mip_mine_faq_down)
                    isFaq3 = false
                } else {
                    faqText3?.visibility = VISIBLE
                    faq3?.setImageResource(R.mipmap.mip_mine_faq_up)
                    isFaq3 = true
                }
            }
        }
    }

    companion object {
        fun newStart(context: Context) {
            val intent = Intent(context, MineFAQActivity::class.java)
            context.startActivity(intent)
        }
    }
}