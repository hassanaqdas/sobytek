package com.sobytek.erpsobytek.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientApi {

    private var retrofit: Retrofit? = null
    private var baseUrl: String = ""

    fun setBaseUrl(url: String) {
        baseUrl = url
        retrofit = null // Invalidate the old instance
    }

    fun <T> createService(serviceClass: Class<T>): T {
        if (retrofit == null) {
            if (baseUrl.isEmpty()) {
                throw IllegalStateException("Base URL is not set. Call setBaseUrl() before creating a service.")
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(serviceClass)
    }
}