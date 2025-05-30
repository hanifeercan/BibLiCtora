package com.amineaytac.biblictora.core.domain.readingstatus

import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class AddReadingBookItemUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(readingBook: ReadingBook) {
        return bookRepository.addReadingBookItem(readingBook)
    }
}