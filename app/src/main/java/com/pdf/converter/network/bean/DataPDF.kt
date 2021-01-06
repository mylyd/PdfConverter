package com.pdf.converter.network.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author : ydli
 * @time : 2020/12/21 17:17
 * @description :
 */
@Parcelize
class DataPDF(var id: Int, var task_state: String, var token: String) : Parcelable