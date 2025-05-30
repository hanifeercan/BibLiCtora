package com.amineaytac.biblictora.core.domain.favorite

import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.repo.BookRepository
import javax.inject.Inject

class DeleteFavoriteItemUseCase @Inject constructor(private val bookRepository: BookRepository) {

    suspend operator fun invoke(book: Book) {
        return bookRepository.deleteFavoriteItem(book)
    }
}