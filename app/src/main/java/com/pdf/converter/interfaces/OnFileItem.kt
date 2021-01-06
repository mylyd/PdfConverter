package com.pdf.converter.interfaces

import java.io.File

/**
 * @author : ydli
 * @time : 2020/11/18 14:46
 * @description :
 */
interface OnFileItem {
    fun itemWord(file: File) = Unit

    fun itemPDF(file: File) = Unit

    fun itemZip(file: File) = Unit

    fun itemAll(position: Int) = Unit

    fun itemDelete(position: Int, file: File) = Unit

    fun itemRename(position: Int, file: File) = Unit
}