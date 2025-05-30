package com.amineaytac.biblictora.core.domain.quotes

import androidx.lifecycle.LiveData
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class GetQuoteBookUseCase @Inject constructor(private val bookRepository: BookRepository) {
    operator fun invoke(bookId: Int): LiveData<QuoteBook> {
        return bookRepository.getQuoteBook(bookId)
    }
}