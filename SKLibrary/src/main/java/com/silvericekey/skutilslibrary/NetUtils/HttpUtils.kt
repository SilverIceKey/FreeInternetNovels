package com.silvericekey.skutilslibrary.NetUtils

import android.text.TextUtils
import com.silvericekey.skutilslibrary.IOUtils.ThreadUtils
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class HttpUtils {
    private var okHttpClient: OkHttpClient
    private var retrofit: Retrofit

    companion object {
        private var httpUtils: HttpUtils? = null
        private var baseUrl = ""
        fun getInstance(): HttpUtils {
            if (httpUtils == null) {
                synchronized(HttpUtils::class) {
                    if (httpUtils == null) {
                        httpUtils = HttpUtils()
                    }
                }
            }
            return httpUtils!!
        }

        fun setBaseUrl(baseUrl:String){
            this.baseUrl = baseUrl
        }
    }

    constructor() {
        if (TextUtils.isEmpty(baseUrl)){
            throw Exception("Please set base url first")
        }
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    fun <T> obtainClass(clz: Class<T>): T {
        return retrofit.create(clz)
    }

    fun <T> execute(call: Call<T>, callback: NetCallback<T>) {
        ThreadUtils.runOnIOThread {
            var response = call.execute()
            if (callback != null) {
                ThreadUtils.runOnUiThread { callback.onSuccess(response.body()) }
            }
        }
    }
}