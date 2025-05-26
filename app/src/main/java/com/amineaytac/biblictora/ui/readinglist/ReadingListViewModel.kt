package com.amineaytac.biblictora.ui.readinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.domain.readingstatus.DeleteReadingBookItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.GetReadingBookItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingListViewModel @Inject constructor(
    private val getReadingBookItemsUseCase: GetReadingBookItemsUseCase,
    private val deleteReadingBookItemUseCase: DeleteReadingBookItemUseCase
) : ViewModel() {

    private val _readingBookScreenUiState = MutableLiveData<ReadingBookListScreenUiState>()
    val readingBookScreenUiState: LiveData<ReadingBookListScreenUiState> get() = _readingBookScreenUiState

    private lateinit var books: Flow<List<ReadingBook>>

    init {
        getReadingBooksFlowData()
    }

    private fun getReadingBooksFlowData() {
        books = getReadingBookItemsUseCase()
    }

    fun deleteReadingBookItems(readingBook: ReadingBook) {
        viewModelScope.launch {
            deleteReadingBookItemUseCase(readingBook)
        }
    }

    fun getReadingBooks() {
        viewModelScope.launch {
            books.catch {
                _readingBookScreenUiState.postValue(
                    ReadingBookListScreenUiState(
                        isError = true, errorMessage = it.message
                    )
                )
            }.collect { data ->
                _readingBookScreenUiState.postValue(ReadingBookListScreenUiState(data))
            }
        }
    }
}