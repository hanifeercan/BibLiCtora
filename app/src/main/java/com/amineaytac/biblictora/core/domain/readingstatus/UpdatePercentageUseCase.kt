package com.amineaytac.biblictora.core.domain.readingstatus

import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class UpdatePercentageUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(bookId: Int, readingPercentage: Int, readingProgress: Int) {
        return bookRepository.updatePercentage(bookId, readingPercentage, readingProgress)
    }
}