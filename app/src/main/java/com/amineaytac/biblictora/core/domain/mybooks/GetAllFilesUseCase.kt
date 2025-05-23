package com.amineaytac.biblictora.core.domain.mybooks

import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.core.data.repo.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFilesUseCase @Inject constructor(private val bookRepository: BookRepository) {
    operator fun invoke(): Flow<List<MyBooksItem>> {
        return bookRepository.getAllFiles()
    }
}