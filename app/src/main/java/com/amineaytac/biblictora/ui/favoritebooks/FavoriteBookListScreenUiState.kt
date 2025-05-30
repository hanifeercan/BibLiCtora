package com.amineaytac.biblictora.ui.favoritebooks

import com.amineaytac.biblictora.core.data.model.Book

data class FavoriteBookListScreenUiState(
    val books: List<Book> = emptyList(),
    val isError: Boolean = false,
    val errorMessage: String? = ""
)