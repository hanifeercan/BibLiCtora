package com.amineaytac.biblictora.core.network

import android.content.Context
import com.amineaytac.biblictora.R
import com.yagmurerdogan.toasticlib.Toastic

interface NetworkListener {
    fun onNetworkStateChanged(isConnected: Boolean)

    fun onInternetDisconnected(context: Context) {
        Toastic.toastic(
            context = context,
            message = context.getString(R.string.check_internet_connection),
            duration = Toastic.LENGTH_SHORT,
            type = Toastic.ERROR,
            isIconAnimated = true
        ).show()
    }

    fun onInternetConnected(context:Context)
}