package com.amineaytac.biblictora.core.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReadFormats(
    val textHtml: String,
    val textHtmlUtf8: String,
) : Parcelable