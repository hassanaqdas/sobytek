package com.sobytek.erpsobytek.retrofit

import com.sobytek.erpsobytek.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientApi {

    private val client = OkHttpClient.Builder()
        .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun<T> createService(bindService : Class<T>):T{
        return retrofit.create(bindService)
    }
}