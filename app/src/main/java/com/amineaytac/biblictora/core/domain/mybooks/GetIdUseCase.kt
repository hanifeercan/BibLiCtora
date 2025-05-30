package com.amineaytac.biblictora.core.domain.mybooks

import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class GetIdUseCase @Inject constructor(private val bookRepository: BookRepository) {

    suspend operator fun invoke(filePath: String): Int {
        return bookRepository.getId(filePath)
    }
}