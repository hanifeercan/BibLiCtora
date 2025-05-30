package com.amineaytac.biblictora.ui.discover

import androidx.paging.PagingData
import com.amineaytac.biblictora.core.data.model.Book

data class BookListScreenUiState(
    val books: PagingData<Book> = PagingData.empty(),
    val isError: Boolean = false,
    val errorMessage: String? = ""
)