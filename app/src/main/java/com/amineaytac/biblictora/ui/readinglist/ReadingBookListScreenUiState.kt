package com.amineaytac.biblictora.ui.readinglist

import com.amineaytac.biblictora.core.data.model.ReadingBook

data class ReadingBookListScreenUiState(
    val books: List<ReadingBook> = emptyList(),
    val isError: Boolean = false,
    val errorMessage: String? = ""
)