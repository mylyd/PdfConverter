package com.pdf.converter.interfaces

/**
 * @author : ydli
 * @time : 2020/11/18 14:46
 * @description :
 */
interface OnClickItem {

    fun item(position: Int, string: String? = null)

    fun close(position: Int)

}