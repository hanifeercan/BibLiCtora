package com.amineaytac.biblictora.core.network.dto.quotes

import com.google.gson.annotations.SerializedName

data class QuoteResponse(
    @SerializedName("author") val author: String?, @SerializedName("quote") val quote: String?
)