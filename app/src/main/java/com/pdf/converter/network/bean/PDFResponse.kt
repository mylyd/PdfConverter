package com.pdf.converter.network.bean

/**
 * @author : ydli
 * @time : 2020/12/21 16:56
 * @description :
 */
class PDFResponse<T>(
    var data: T,
    var errorCode: Int,
    var errorMsg: String,
    var sid: String
)