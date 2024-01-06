package com.example.paymentapp.network

import com.example.paymentapp.network.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.*
import java.io.File
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private const val BASE_URL = Config.BASE_URL
    private lateinit var cache: Cache

    private fun createOkHttpClient(context: Context): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cacheSize = 10 * 1024 * 1024L // 10 MB
        cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor { chain ->
                var request = chain.request()
                request = if (isNetworkAvailable(context))
                    request.newBuilder().header("Cache-Control", "public, max-age=5").build()
                else
                    request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=86400").build()
                chain.proceed(request)
            }
            .cache(cache)
            .connectTimeout(1, TimeUnit.SECONDS)  // Set the connect timeout
            .readTimeout(1, TimeUnit.SECONDS)    // Set the read timeout
            .writeTimeout(1, TimeUnit.SECONDS)   // Set the write timeout
            .build()
    }

    fun clearCache() {
        if (this::cache.isInitialized) {
            try {
                val sizeBefore = cache.size()
                cache.evictAll()
                val sizeAfter = cache.size()
                Log.d("CacheClear", "Cache cleared. Size before: $sizeBefore, Size after: $sizeAfter")
            } catch (e: Exception) {
                // Handle or log the exception as necessary
            }
        }
    }


    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.getActiveNetwork()
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun buildRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getGroupApiService(context: Context): ApiService {
        val retrofit = buildRetrofit(context)
        return retrofit.create(ApiService::class.java)
    }

}
