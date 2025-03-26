package com.amineaytac.biblictora.core.network.source.randomquote

import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import retrofit2.Response
import javax.inject.Inject

class RandomQuoteDataSourceImpl @Inject constructor(private val randomQuoteApi: RandomQuoteApi) :
    RandomQuoteDataSource {
    override suspend fun getRandomQuote(): Response<QuoteResponse> {
        return randomQuoteApi.getRandomQuote()
    }
}