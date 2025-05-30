package com.amineaytac.biblictora.core.domain.readingstatus

import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class UpdateBookStatusAndPercentageUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(itemId: Int, readingStates: String, readingPercentage: Int) {
        return bookRepository.updateBookStatusAndPercentage(
            itemId,
            readingStates,
            readingPercentage
        )
    }
}