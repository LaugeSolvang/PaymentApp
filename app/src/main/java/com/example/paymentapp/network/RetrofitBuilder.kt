package com.example.paymentapp.network

import com.example.paymentapp.network.api.GroupApiService
import com.google.android.datatransport.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private const val BASE_URL = Config.BASE_URL

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val groupApiService: GroupApiService by lazy {
        retrofit.create(GroupApiService::class.java)
    }
}
