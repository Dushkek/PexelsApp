package com.adush.pexelsapp.data.network.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtil {

    fun isNetworkAvailable(context: Context) : Boolean{
        val connectivityService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return networkCheck(connectivityService)
    }

    private fun networkCheck(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)
        return connection != null && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
}