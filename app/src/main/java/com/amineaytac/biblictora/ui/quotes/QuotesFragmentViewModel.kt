package com.amineaytac.biblictora.ui.quotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.core.domain.quotes.GetQuoteBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesFragmentViewModel @Inject constructor(
    private val getQuoteBooksUseCase: GetQuoteBooksUseCase
) : ViewModel() {

    private val _quoteBooksScreenUiState = MutableLiveData<QuoteBookListScreenUiState>()
    val quoteBooksScreenUiState: LiveData<QuoteBookListScreenUiState> get() = _quoteBooksScreenUiState

    private lateinit var books: Flow<List<QuoteBook>>

    init {
        getQuoteBooksFlowData()
    }

    private fun getQuoteBooksFlowData() {
        viewModelScope.launch {
            books = getQuoteBooksUseCase()
        }
    }

    fun getQuoteBooks() {
        viewModelScope.launch {
            books.catch {
                _quoteBooksScreenUiState.postValue(
                    QuoteBookListScreenUiState(
                        isError = true, errorMessage = it.message
                    )
                )
            }.collect { data ->
                _quoteBooksScreenUiState.postValue(QuoteBookListScreenUiState(data))
            }
        }
    }
}