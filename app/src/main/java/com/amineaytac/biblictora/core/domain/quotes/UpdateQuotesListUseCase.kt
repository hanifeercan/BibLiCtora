package com.amineaytac.biblictora.core.domain.quotes

import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class UpdateQuotesListUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(bookId: Int, updatedList: List<String>) {
        val quoteItems = updatedList.map { QuoteItem(quote = it) }
        return bookRepository.updateQuotesList(bookId, quoteItems)
    }
}