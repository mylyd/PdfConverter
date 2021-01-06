package com.pdf.converter.network

import com.pdf.converter.interfaces.Request
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author : ydli
 * @time : 2020/11/11 8:56
 * @description :
 */
enum class RetrofitNetwork {
    INSTANCE;

    var mRetrofit: Retrofit? = null

    private fun init() {
        // 初始化okhttp
        val client = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        // 初始化Retrofit
        mRetrofit =
            Retrofit.Builder()
                .client(client)
                .baseUrl(Request.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
    }

    fun <T> getRequest(cls: Class<T>): T = mRetrofit!!.create(cls)

    inline fun <reified T> getRequest(): T = getRequest(T::class.java)

    companion object;

    init {
        init()
    }
}