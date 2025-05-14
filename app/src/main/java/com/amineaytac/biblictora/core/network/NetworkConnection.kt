@file:Suppress("DEPRECATION")

package com.amineaytac.biblictora.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import androidx.lifecycle.LiveData

class NetworkConnection(context: Context) : LiveData<Boolean>() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }
    }

    private var isCallbackRegistered = false

    override fun onActive() {
        super.onActive()
        updateConnection()
        if (!isCallbackRegistered) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            isCallbackRegistered = true
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (isCallbackRegistered) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            } catch (_: IllegalArgumentException) {
            }
            isCallbackRegistered = false
        }
    }

    private fun updateConnection() {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}