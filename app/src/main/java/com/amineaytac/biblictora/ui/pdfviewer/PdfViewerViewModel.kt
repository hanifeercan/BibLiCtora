package com.amineaytac.biblictora.ui.pdfviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.domain.mybooks.GetLastPageUseCase
import com.amineaytac.biblictora.core.domain.mybooks.UpdateLastPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val updateLastPageUseCase: UpdateLastPageUseCase,
    private val getLastPageUseCase: GetLastPageUseCase
) : ViewModel() {

    private val _lastPage = MutableLiveData<Int>()
    val lastPage: LiveData<Int> get() = _lastPage

    fun getLastPage(filePath: String) {
        viewModelScope.launch {
            _lastPage.value = getLastPageUseCase(filePath)
        }
    }

    fun updateLastPage(filePath: String, lastPage: Int) {
        viewModelScope.launch {
            updateLastPageUseCase(filePath, lastPage)
        }
    }
}