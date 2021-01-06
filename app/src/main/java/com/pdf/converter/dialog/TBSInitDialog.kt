package com.pdf.converter.dialog

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.pdf.converter.R
import com.pdf.converter.interfaces.OnDeleteClick
import kotlinx.android.synthetic.main.dialog_delete.*

/**
 * @author : ydli
 * @time : 2020/12/24 11:20
 * @description :
 */
class TBSInitDialog(activity: Activity) : BaseDialog(activity, R.style.dialog_soft_input) {
    private var ok: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_init_tbs)
        setCanceledOnTouchOutside(false)
        windows?.setWindowAnimations(R.style.center_dialog_anim_style)
        windows?.setGravity(Gravity.CENTER)
        ok = findViewById(R.id.dialog_ok)
        ok?.setOnClickListener { dismiss() }
    }
}