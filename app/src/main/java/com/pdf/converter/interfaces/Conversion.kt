package com.pdf.converter.interfaces

import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/28 17:36
 * @description :
 */
interface Conversion {

    fun success(file: File) = Unit

    fun fail(throws: String) = Unit

    fun download(progress: Int) = Unit

    fun startDownload() = Unit

}