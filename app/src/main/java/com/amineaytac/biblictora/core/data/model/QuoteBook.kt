package com.amineaytac.biblictora.core.data.model

data class QuoteBook (
    val id: Int,
    val authors: String,
    val title: String,
    val image: String,
    val quotesList: List<String>
)