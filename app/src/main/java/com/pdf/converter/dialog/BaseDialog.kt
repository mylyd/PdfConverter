package com.pdf.converter.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import com.pdf.converter.R

/**
 * @Description: Dialog基类  主要处理低端机型上顶部显示一条绿线的bug及其他参数配置
 * @Author:
 * @CreateDate:
 */
open class BaseDialog : Dialog {
    protected var windows: Window? = null

    constructor(context: Context) : super(context)

    constructor(context: Context?, themeResId: Int) : super(context!!, themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dividerId =
            context.resources.getIdentifier("android:id/titleDivider", null, null)
        val divider = findViewById<View>(dividerId)
        divider?.setBackgroundColor(Color.TRANSPARENT)
        setCanceledOnTouchOutside(true) //设置点击外部区域是否可以取消
        windows = this.window
        windows?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}