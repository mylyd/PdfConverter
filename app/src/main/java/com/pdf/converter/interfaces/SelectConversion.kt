package com.pdf.converter.interfaces

import java.io.File

/**
 * @author : ydli
 * @time : 2021/01/11 18:18
 * @description :
 */
interface SelectConversion {

    fun pdf2Word(file: File?) = Unit

    fun pdf2Img(file: File?) = Unit
}