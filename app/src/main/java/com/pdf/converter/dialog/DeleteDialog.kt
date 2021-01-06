package com.pdf.converter.dialog

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.pdf.converter.R
import com.pdf.converter.interfaces.OnDeleteClick

/**
 * @author : ydli
 * @time : 2020/12/24 11:20
 * @description :
 */
class DeleteDialog(activity: Activity, item: OnDeleteClick) :
    BaseDialog(activity, R.style.dialog_soft_input) {
    private var deleteClick: OnDeleteClick = item
    private var position: Int = -1
    private var icon: ImageView? = null
    private var textContent: TextView? = null
    private var cancel: TextView? = null
    private var ok: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete)
        windows?.setWindowAnimations(R.style.center_dialog_anim_style)
        windows?.setGravity(Gravity.CENTER)
        cancel = findViewById(R.id.cancel)
        icon = findViewById(R.id.icon)
        textContent = findViewById(R.id.text_content)
        cancel = findViewById(R.id.cancel)
        cancel?.setOnClickListener {
            deleteClick.cancel()
            dismiss()
        }
        ok = findViewById(R.id.ok)
        ok?.setOnClickListener {
            deleteClick.ok(position)
            dismiss()
        }
    }

    fun show(position: Int) {
        this.position = position
        super.show()
    }

    fun showCancel(){
        super.show()
        icon?.visibility = View.GONE
        textContent?.text = textContent?.context?.getString(R.string.cancel_content)
        cancel?.text = cancel?.context?.getString(R.string.yes)
        ok?.text = ok?.context?.getString(R.string.no)
    }
}