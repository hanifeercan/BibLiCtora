package com.amineaytac.biblictora.core.domain.quotes

import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.core.data.repo.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuoteBooksUseCase @Inject constructor(private val bookRepository: BookRepository) {
    operator fun invoke(): Flow<List<QuoteBook>> {
        return bookRepository.getQuoteBooks()
    }
}