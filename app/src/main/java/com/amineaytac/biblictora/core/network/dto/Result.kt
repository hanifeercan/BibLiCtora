package com.amineaytac.biblictora.core.network.dto

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("authors")
    val authors: List<Author?>?,
    @SerializedName("bookshelves")
    val bookshelves: List<String?>?,
    @SerializedName("copyright")
    val copyright: Boolean?,
    @SerializedName("download_count")
    val downloadCount: Int?,
    @SerializedName("formats")
    val formats: Formats?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("languages")
    val languages: List<String?>?,
    @SerializedName("media_type")
    val mediaType: String?,
    @SerializedName("subjects")
    val subjects: List<String?>?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("translators")
    val translators: List<Translator?>?
)