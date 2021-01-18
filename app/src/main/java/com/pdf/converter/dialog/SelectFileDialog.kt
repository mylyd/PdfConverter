package com.pdf.converter.dialog

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.pdf.converter.R
import com.pdf.converter.interfaces.SelectConversion
import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/24 11:26
 * @description :
 */
class SelectFileDialog(activity: Activity, click: SelectConversion) :
    BaseDialog(activity, R.style.dialog_soft_input) {
    private var selectorClick: SelectConversion = click
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select)
        windows?.setGravity(Gravity.BOTTOM)
        windows?.setWindowAnimations(R.style.bottom_dialog_anim_style)
        windows?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        findViewById<TextView>(R.id.to_word).setOnClickListener {
            selectorClick.pdf2Word(file)
            dismiss()
        }
        findViewById<TextView>(R.id.to_img).setOnClickListener {
            selectorClick.pdf2Img(file)
            dismiss()
        }
    }

    fun show(file: File) {
        this.file = file
        super.show()
    }

}