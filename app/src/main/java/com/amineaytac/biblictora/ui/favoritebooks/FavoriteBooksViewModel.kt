package com.amineaytac.biblictora.ui.favoritebooks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.domain.favorite.DeleteFavoriteItemUseCase
import com.amineaytac.biblictora.core.domain.favorite.GetFavoriteItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteBooksViewModel @Inject constructor(
    private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
    private val deleteFavoriteItemUseCase: DeleteFavoriteItemUseCase
) : ViewModel() {

    private val _favoriteBooksScreenUiState = MutableLiveData<FavoriteBookListScreenUiState>()
    val favoriteBooksScreenUiState: LiveData<FavoriteBookListScreenUiState> get() = _favoriteBooksScreenUiState

    private lateinit var books: Flow<List<Book>>

    init {
        getFavoriteBooksFlowData()
    }

    private fun getFavoriteBooksFlowData() {
        viewModelScope.launch {
            books = getFavoriteItemsUseCase()
        }
    }

    fun getFavoriteBooks() {
        viewModelScope.launch {
            books.catch {
                _favoriteBooksScreenUiState.postValue(
                    FavoriteBookListScreenUiState(
                        isError = true, errorMessage = it.message
                    )
                )
            }.collect { data ->
                _favoriteBooksScreenUiState.postValue(FavoriteBookListScreenUiState(data))
            }
        }
    }

    fun deleteFavoriteBook(book: Book) {
        viewModelScope.launch {
            deleteFavoriteItemUseCase.invoke(book)
        }
    }
}