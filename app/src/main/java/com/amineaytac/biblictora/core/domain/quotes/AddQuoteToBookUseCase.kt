package com.amineaytac.biblictora.core.domain.quotes

import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class AddQuoteToBookUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(readingBook: ReadingBook, newQuote: String) {
        return bookRepository.addQuoteToBook(readingBook, newQuote)
    }
}