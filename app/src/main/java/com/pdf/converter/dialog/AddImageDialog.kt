package com.pdf.converter.dialog

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.pdf.converter.R
import com.pdf.converter.interfaces.OnSelectorImage

/**
 * @author : ydli
 * @time : 2020/12/24 11:26
 * @description :
 */
class AddImageDialog(activity: Activity,click: OnSelectorImage) : BaseDialog(activity, R.style.dialog_soft_input) {
    private var selectorClick: OnSelectorImage = click

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_selectro)
        windows?.setGravity(Gravity.BOTTOM)
        windows?.setWindowAnimations(R.style.bottom_dialog_anim_style)
        windows?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        findViewById<TextView>(R.id.camera).setOnClickListener {
            selectorClick.camera()
            dismiss()
        }
        findViewById<TextView>(R.id.album).setOnClickListener {
            selectorClick.album()
            dismiss()
        }
        findViewById<TextView>(R.id.cancel).setOnClickListener {
            selectorClick.cancel()
            dismiss()
        }
    }

}