package com.amineaytac.biblictora.ui.mybooks

import com.amineaytac.biblictora.core.data.model.MyBooksItem

data class MyBooksScreenUiState(
    val pdfs: List<MyBooksItem> = emptyList(),
    val isError: Boolean = false,
    val errorMessage: String? = ""
)