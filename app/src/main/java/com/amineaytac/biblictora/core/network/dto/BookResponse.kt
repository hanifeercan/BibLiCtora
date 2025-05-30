package com.amineaytac.biblictora.core.network.dto


import com.google.gson.annotations.SerializedName

data class BookResponse(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("next")
    val next: String?,
    @SerializedName("previous")
    val previous: Any?,
    @SerializedName("results")
    val results: List<Result?>?
)