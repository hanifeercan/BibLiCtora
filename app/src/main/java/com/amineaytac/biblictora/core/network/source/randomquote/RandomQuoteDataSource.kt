package com.amineaytac.biblictora.core.network.source.randomquote

import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import retrofit2.Response

interface RandomQuoteDataSource {
    suspend fun getRandomQuote(): Response<QuoteResponse>
}