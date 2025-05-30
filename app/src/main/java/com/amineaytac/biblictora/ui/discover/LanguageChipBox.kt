package com.amineaytac.biblictora.ui.discover

import androidx.annotation.DrawableRes
data class LanguageChipBox(
    val id: Int,
    val text: String,
    val abbreviation: String,
    @DrawableRes
    val iconFlag: Int
)