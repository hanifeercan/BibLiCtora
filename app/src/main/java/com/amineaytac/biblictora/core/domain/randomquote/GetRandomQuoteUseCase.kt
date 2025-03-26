package com.amineaytac.biblictora.core.domain.randomquote

import com.amineaytac.biblictora.core.common.ResponseState
import com.amineaytac.biblictora.core.data.repo.BookRepository
import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRandomQuoteUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(): Flow<ResponseState<QuoteResponse>> {
        return bookRepository.getRandomQuote()
    }
}