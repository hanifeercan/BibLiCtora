package com.amineaytac.biblictora.ui.pdfviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.repo.BookRepository
import com.amineaytac.biblictora.core.domain.mybooks.UpdateLastPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val updateLastPageUseCase: UpdateLastPageUseCase,
    private val bookRepository: BookRepository
) : ViewModel() {

    suspend fun getLastPage(filePath: String): Int {
        return bookRepository.getLastPage(filePath)
    }

    fun updateLastPage(filePath: String, lastPage: Int) {
        viewModelScope.launch {
            updateLastPageUseCase(filePath, lastPage)
        }
    }
}