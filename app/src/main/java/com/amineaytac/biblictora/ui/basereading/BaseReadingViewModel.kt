package com.amineaytac.biblictora.ui.basereading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.domain.quotes.AddQuoteToBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseReadingViewModel @Inject constructor(
    private val addQuoteToBookUseCase: AddQuoteToBookUseCase
) : ViewModel() {

    fun addQuoteToBook(readingBook: ReadingBook, newQuote: String) {
        viewModelScope.launch {
            addQuoteToBookUseCase(readingBook, newQuote)
        }
    }
}