package com.adush.pexelsapp.data.network

import android.content.Context
import com.adush.pexelsapp.BuildConfig
import com.adush.pexelsapp.data.network.configuration.MAPIConfig
import com.adush.pexelsapp.data.network.interceptors.AuthInterceptor
import com.adush.pexelsapp.data.network.interceptors.CacheInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiFactory {

    private lateinit var apiService: ApiService

    fun getApiService(context: Context): ApiService {
        if (!::apiService.isInitialized) {
            createApiService(context)
        }
        return apiService
    }

    private fun createApiService(context: Context) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        val cacheSize = (10 * 1024 * 1024).toLong()
        val cache = Cache(context.cacheDir, cacheSize)

        cacheClearAfterTime(cache)

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(BuildConfig.PEXELS_API_KEY))
            .addInterceptor(CacheInterceptor(context))
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20L, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(MAPIConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun cacheClearAfterTime(cache: Cache) {
        val currentTime = System.currentTimeMillis()
        val lastModified = cache.directory.lastModified()
        val hour = (60 * 60 * 1000)
        if (cache.directory.exists() && (currentTime - lastModified > hour) ) {
            cache.evictAll()
        }
    }
}