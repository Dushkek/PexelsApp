package com.adush.pexelsapp.data.network.interceptors

import android.content.Context
import com.adush.pexelsapp.data.network.utils.NetworkUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class CacheInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val cacheControl = if (NetworkUtil.isNetworkAvailable(context)) {
            CacheControl.Builder()
                .maxAge(1, TimeUnit.MINUTES)
                .build()
        } else {
            CacheControl.Builder()
                .onlyIfCached()
                .maxStale(1, TimeUnit.HOURS)
                .build()
        }

        request = request.newBuilder()
            .cacheControl(cacheControl)
            .build()

        return chain.proceed(request)
    }
}