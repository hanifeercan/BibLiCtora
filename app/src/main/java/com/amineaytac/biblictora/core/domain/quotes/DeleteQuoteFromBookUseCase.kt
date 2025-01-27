package com.amineaytac.biblictora.core.domain.quotes

import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class DeleteQuoteFromBookUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(bookId: Int, quoteToRemove: String) {
        return bookRepository.deleteQuoteFromBook(bookId, quoteToRemove)
    }
}