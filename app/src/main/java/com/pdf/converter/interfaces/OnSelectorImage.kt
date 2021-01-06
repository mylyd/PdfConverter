package com.pdf.converter.interfaces

/**
 * @author : ydli
 * @time : 2020/12/24 11:26
 * @description :
 */
interface OnSelectorImage {

    fun cancel() = Unit

    fun camera() = Unit

    fun album() = Unit
}