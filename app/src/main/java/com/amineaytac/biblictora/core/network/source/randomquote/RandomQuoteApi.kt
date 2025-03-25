package com.amineaytac.biblictora.core.network.source.randomquote

import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import retrofit2.Response
import retrofit2.http.GET

interface RandomQuoteApi {
    @GET(".")
    suspend fun getRandomQuote(): Response<QuoteResponse>
}