package com.amineaytac.biblictora.core.domain.readingstatus

import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReadingBookItemsUseCase @Inject constructor(private val bookRepository: BookRepository) {
    operator fun invoke(): Flow<List<ReadingBook>> {
        return bookRepository.getReadingBookItems()
    }
}