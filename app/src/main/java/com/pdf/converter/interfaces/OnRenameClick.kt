package com.pdf.converter.interfaces

import android.widget.EditText
import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/24 11:22
 * @description :
 */
interface OnRenameClick {
    fun cancel() = Unit

    fun ok(position: Int, filePath: File? = null, editText: String? = null) = Unit
}