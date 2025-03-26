package com.amineaytac.biblictora.core.domain.mybooks

import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class AddFileItemUseCase @Inject constructor(private val bookRepository: BookRepository) {
    suspend operator fun invoke(myBooksItem: MyBooksItem) {
        return bookRepository.addFileItem(myBooksItem)
    }
}