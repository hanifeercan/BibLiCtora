package com.amineaytac.biblictora.core.domain.readingstatus

import androidx.lifecycle.LiveData
import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class IsBookItemReadingUseCase @Inject constructor(private val bookRepository: BookRepository) {
    operator fun invoke(itemId: String): LiveData<Boolean> {
        return bookRepository.isBookItemReading(itemId)
    }
}