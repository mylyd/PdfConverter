package com.pdf.converter.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.donkingliang.imageselector.utils.StringUtils
import com.pdf.converter.R
import com.pdf.converter.interfaces.OnRenameClick
import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/24 11:20
 * @description :
 */
class RenameDialog(private val activity: Activity, item: OnRenameClick) :
    BaseDialog(activity, R.style.dialog_soft_input) {
    private var deleteClick: OnRenameClick = item
    private var position: Int = -1
    private var file: File? = null
    private var renameEdit: EditText? = null
    private var cancel: TextView? = null
    private var ok: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rename)
        windows?.setWindowAnimations(R.style.center_dialog_anim_style)
        windows?.setGravity(Gravity.CENTER)
        cancel = findViewById(R.id.cancel)
        renameEdit = findViewById(R.id.rename_edit)
        cancel = findViewById(R.id.cancel)
        cancel?.setOnClickListener {
            deleteClick.cancel()
            dismiss()
        }
        ok = findViewById(R.id.ok)
        ok?.setOnClickListener {
            val text = renameEdit?.text.toString()
            if (StringUtils.isEmptyString(text)) {
                Toast.makeText(activity, "File name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidFileName(text)) {
                Toast.makeText(
                    activity,
                    "Special characters are not allowed,Please try again",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            deleteClick.ok(position, file, text)
            dismiss()
        }
        setOnShowListener {
            showKeyBoard()
            if (file != null) {
                val namePath = file?.path.toString().split(".")[0]
                val name = namePath.split("/")
                renameEdit?.text = Editable.Factory.getInstance().newEditable(name[name.size - 1])
            }
        }
    }

    private fun isValidFileName(fileName: String?): Boolean =
        fileName!!.length > 255 || fileName.matches(Regex("""[^/\\\\<>*?|\"]+"""))


    private fun showKeyBoard() {
        renameEdit?.postDelayed(Runnable {
            val manager =
                renameEdit?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(renameEdit, 0)
        }, 500)
    }

    fun show(position: Int, file: File? = null) {
        this.position = position
        this.file = file
        super.show()
    }

}