package com.pdf.converter.interfaces

/**
 * @author : ydli
 * @time : 2020/12/24 11:22
 * @description :
 */
interface OnDeleteClick {
    fun cancel() = Unit

    fun ok(position: Int) = Unit
}