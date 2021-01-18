package com.pdf.converter.interfaces

import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/24 11:22
 * @description :
 */
interface OnDeleteClick {
    fun cancel() = Unit

    fun ok(position: Int, filePath: File? = null) = Unit
}