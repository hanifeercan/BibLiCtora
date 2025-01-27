package com.amineaytac.biblictora.core.domain.quotes

import androidx.lifecycle.LiveData
import com.amineaytac.biblictora.core.data.repo.BookRepository
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import javax.inject.Inject

class GetQuoteBookUseCase @Inject constructor(private val bookRepository: BookRepository) {
    operator fun invoke(bookId: Int): LiveData<QuotesEntity> {
        return bookRepository.getQuoteBook(bookId)
    }
}