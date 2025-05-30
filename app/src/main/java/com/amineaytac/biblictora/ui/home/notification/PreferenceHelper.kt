package com.amineaytac.biblictora.ui.home.notification

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("PrefFile", Context.MODE_PRIVATE)

    fun setHourAndMinute(hour: Int, minute: Int) {
        val editor = prefs.edit()
        editor.putInt("hour", hour)
        editor.putInt("minute", minute)
        editor.apply()
    }

    fun getHour(): Int {
        return prefs.getInt("hour", -1)
    }

    fun getMinute(): Int {
        return prefs.getInt("minute", -1)
    }
}