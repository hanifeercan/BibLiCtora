package com.amineaytac.biblictora.ui.quotes

import com.amineaytac.biblictora.core.data.model.QuoteBook

data class QuoteBookListScreenUiState(
    val books: List<QuoteBook> = emptyList(),
    val isError: Boolean = false,
    val errorMessage: String? = ""
)