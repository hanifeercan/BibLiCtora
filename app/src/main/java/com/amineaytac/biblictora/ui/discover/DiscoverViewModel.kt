package com.amineaytac.biblictora.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.domain.rest.GetAllBooksUseCase
import com.amineaytac.biblictora.core.domain.rest.GetBooksWithLanguagesUseCase
import com.amineaytac.biblictora.core.domain.rest.GetBooksWithSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getAllBooksUseCase: GetAllBooksUseCase,
    private val getBooksWithSearchUseCase: GetBooksWithSearchUseCase,
    private val getBooksWithLanguagesUseCase: GetBooksWithLanguagesUseCase
) : ViewModel() {

    private val _bookScreenUiState = MutableLiveData<BookListScreenUiState>()
    val bookScreenUiState: LiveData<BookListScreenUiState> get() = _bookScreenUiState

    private var chipClickStates = Array(12) { false }
    private var searchText = ""
    private var isChipGroupVisible = false

    private lateinit var allBooksFlow: Flow<PagingData<Book>>
    private lateinit var searchBookFlow: Flow<PagingData<Book>>
    private lateinit var languagesBookFlow: Flow<PagingData<Book>>
    var isFirstRest = true

    init {
        getAllBooksFlow()
    }

    fun getAllBooksFlow() {
        allBooksFlow = getAllBooksUseCase()
    }

    fun getBooksWithLanguagesFlow(languages: List<String>) {
        viewModelScope.launch {
            languagesBookFlow = getBooksWithLanguagesUseCase(languages).cachedIn(this)
        }
    }

    fun getBooksWithSearchFlow(search: String, languages: List<String>) {
        viewModelScope.launch {
            searchBookFlow = getBooksWithSearchUseCase(search, languages).cachedIn(this)
        }
    }

    fun getAllBooks() {
        isFirstRest = false
        viewModelScope.launch {
            allBooksFlow.catch {
                _bookScreenUiState.postValue(
                    BookListScreenUiState(
                        isError = true, errorMessage = it.message
                    )
                )
            }.collect { data ->
                _bookScreenUiState.postValue(BookListScreenUiState(data))
            }
        }
    }

    fun getBooksWithSearch() {
        viewModelScope.launch {
            searchBookFlow.catch {
                _bookScreenUiState.postValue(
                    BookListScreenUiState(
                        isError = true, errorMessage = it.message
                    )
                )
            }.collect { data ->
                _bookScreenUiState.postValue(BookListScreenUiState(data))
            }
        }
    }

    fun getBooksWithLanguages() {
        viewModelScope.launch {
            languagesBookFlow.catch {
                _bookScreenUiState.postValue(
                    BookListScreenUiState(
                        isError = true, errorMessage = it.message
                    )
                )
            }.collect { data ->
                _bookScreenUiState.postValue(BookListScreenUiState(data))
            }
        }
    }

    fun setChipClickListener(position: Int) {
        chipClickStates[position] = !chipClickStates[position]
    }

    fun getChipClickStates(): Array<Boolean> {
        return chipClickStates
    }

    fun setSearchText(str: String) {
        searchText = str
    }

    fun getSearchText(): String {
        return searchText
    }

    fun setChipGroupVisibility() {
        isChipGroupVisible = !isChipGroupVisible
    }

    fun getChipGroupVisibility(): Boolean {
        return isChipGroupVisible
    }
}