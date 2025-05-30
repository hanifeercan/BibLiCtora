package com.amineaytac.biblictora.ui.basereading

import android.content.Context

class ReadingStyleManager(context: Context) {

    private val sharedPref = context.getSharedPreferences("ReadingStylePref", Context.MODE_PRIVATE)

    data class ReadingStyle(
        val backgroundColorHex: String = "#FFFFFF",
        val textColorHex: String = "#000000",
        val textFontSize: Int = 18
    )

    fun saveReadingStyle(style: ReadingStyle) {
        with(sharedPref.edit()) {
            putString("backgroundColorHex", style.backgroundColorHex)
            putString("textColorHex", style.textColorHex)
            putInt("textFontSize", style.textFontSize)
            apply()
        }
    }

    fun loadReadingStyle(): ReadingStyle {
        return ReadingStyle(
            backgroundColorHex = sharedPref.getString("backgroundColorHex", "#FFFFFF") ?: "#FFFFFF",
            textColorHex = sharedPref.getString("textColorHex", "#000000") ?: "#000000",
            textFontSize = sharedPref.getInt("textFontSize", 18)
        )
    }
}