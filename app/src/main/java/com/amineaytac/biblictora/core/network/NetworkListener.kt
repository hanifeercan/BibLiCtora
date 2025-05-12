package com.amineaytac.biblictora.core.network

interface NetworkListener {
    fun onNetworkStateChanged(isConnected: Boolean)
}