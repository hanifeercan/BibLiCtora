package com.amineaytac.biblictora.core.domain.rest

import androidx.paging.PagingData
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.repo.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBooksWithSearchUseCase @Inject constructor(private val bookRepository: BookRepository) {
    operator fun invoke(
        search: String, languages: List<String>
    ): Flow<PagingData<Book>> {
        return bookRepository.getBooksWithSearch(search, languages, "getBooksWithSearch")
    }
}