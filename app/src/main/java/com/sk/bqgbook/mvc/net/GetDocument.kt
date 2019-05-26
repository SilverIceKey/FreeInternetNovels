package com.sk.bqgbook.mvc.net

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GetDocument {
    @GET("/{menu}")
    open fun getDocument(@Path("menu") menu:String): Call<String>
    @GET("/{bookcode}/all.html")
    open fun getMenu(@Path("bookcode") bookcode:String): Call<String>

}