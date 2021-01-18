package com.pdf.converter.interfaces

import com.pdf.converter.network.Type
import com.pdf.converter.network.bean.DataPDF
import com.pdf.converter.network.bean.PDFResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable

interface Request {
    companion object {
        //const val HOST = "https://snbmvc.jkhench.com/task/api/"
        const val HOST = "https://www.mobopdf.com/task/api/"
    }

    @Headers(Type.cookie)
    @POST("apply")
    fun apply(@QueryMap params: MutableMap<String, String>): Call<PDFResponse<DataPDF>>?

    @Multipart
    @Headers(Type.cookie)
    @POST("upload")
    fun upload(@Part file: MultipartBody.Part,
               @QueryMap params: MutableMap<String, String>): Call<PDFResponse<Boolean>>?

    @Headers(Type.cookie)
    @POST("finisUpload")
    @FormUrlEncoded
    fun finisUpload(@Field("task_id") id: Int): Call<PDFResponse<Boolean>>?

    @Headers(Type.cookie)
    @GET("detail")
    fun detail(@Query("task_id") id: Int) : Call<PDFResponse<DataPDF>>?

    @Headers(Type.cookie)
    @GET("download")
    @Streaming
    fun download(@QueryMap params: MutableMap<String, String>) : Observable<ResponseBody>?

}