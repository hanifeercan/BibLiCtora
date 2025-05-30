package com.amineaytac.biblictora.util

import android.graphics.Color
import android.view.View

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Int.lightenColor(factor: Float = 0.6f): Int {
    this.let { color ->
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val newRed = red + ((255 - red) * factor).toInt()
        val newGreen = green + ((255 - green) * factor).toInt()
        val newBlue = blue + ((255 - blue) * factor).toInt()

        return Color.rgb(
            newRed.coerceIn(0, 255), newGreen.coerceIn(0, 255), newBlue.coerceIn(0, 255)
        )
    }
}