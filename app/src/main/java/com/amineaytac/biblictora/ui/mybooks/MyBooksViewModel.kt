package com.amineaytac.biblictora.ui.mybooks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.core.data.repo.BookRepository
import com.amineaytac.biblictora.core.domain.mybooks.AddFileItemUseCase
import com.amineaytac.biblictora.core.domain.mybooks.DeleteFileItemUseCase
import com.amineaytac.biblictora.core.domain.mybooks.GetAllFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBooksViewModel @Inject constructor(
    private val addFileItemUseCase: AddFileItemUseCase,
    private val deleteFileItemUseCase: DeleteFileItemUseCase,
    private val getAllFilesUseCase: GetAllFilesUseCase,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _myBooksScreenUiState = MutableLiveData<MyBooksScreenUiState>()
    val myBooksScreenUiState: LiveData<MyBooksScreenUiState> get() = _myBooksScreenUiState

    private lateinit var myBooksItem: Flow<List<MyBooksItem>>

    init {
        getAllFiles()
    }

    private fun getAllFiles() {
        viewModelScope.launch {
            myBooksItem = getAllFilesUseCase()
        }
    }

    fun getFiles() {
        viewModelScope.launch {
            myBooksItem.catch {
                _myBooksScreenUiState.postValue(
                    MyBooksScreenUiState(
                        isError = true, errorMessage = it.message
                    )
                )
            }.collect { data ->
                _myBooksScreenUiState.postValue(MyBooksScreenUiState(data))
            }
        }
    }

    fun deleteFileItem(myBooksItem: MyBooksItem) {
        viewModelScope.launch {
            deleteFileItemUseCase.invoke(myBooksItem)
        }
    }

    fun addFileItem(myBooksItem: MyBooksItem) {
        viewModelScope.launch {
            addFileItemUseCase(myBooksItem)
        }
    }

    suspend fun getLastPage(filePath: String): Int {
        return bookRepository.getLastPage(filePath)
    }
}