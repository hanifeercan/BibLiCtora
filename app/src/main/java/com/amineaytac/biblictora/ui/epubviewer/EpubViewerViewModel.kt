package com.amineaytac.biblictora.ui.epubviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.BookRepository
import com.amineaytac.biblictora.core.domain.mybooks.GetIdUseCase
import com.amineaytac.biblictora.core.domain.mybooks.UpdateLastPageUseCase
import com.amineaytac.biblictora.core.domain.quotes.AddQuoteToBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpubViewerViewModel @Inject constructor(
    private val updateLastPageUseCase: UpdateLastPageUseCase,
    private val bookRepository: BookRepository,
    private val getIdUseCase: GetIdUseCase,
    private val addQuoteToBookUseCase: AddQuoteToBookUseCase
) : ViewModel() {

    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int> get() = _id

    suspend fun getLastPage(filePath: String): Int {
        return bookRepository.getLastPage(filePath)
    }

    fun updateLastPage(filePath: String, lastPage: Int) {
        viewModelScope.launch {
            updateLastPageUseCase(filePath, lastPage)
        }
    }

    fun getId(filePath: String) {
        viewModelScope.launch {
            _id.value = getIdUseCase.invoke(filePath)
        }
    }

    fun addQuoteToBook(readingBook: ReadingBook, newQuote: String) {
        viewModelScope.launch {
            addQuoteToBookUseCase(readingBook, newQuote)
        }
    }
}