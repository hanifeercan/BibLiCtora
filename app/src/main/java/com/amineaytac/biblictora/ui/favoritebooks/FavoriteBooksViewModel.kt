package com.amineaytac.biblictora.ui.favoritebooks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.domain.favorite.AddFavoriteItemUseCase
import com.amineaytac.biblictora.core.domain.favorite.DeleteFavoriteItemUseCase
import com.amineaytac.biblictora.core.domain.favorite.GetFavoriteItemsUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.AddReadingBookItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.DeleteReadingBookItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.GetBookItemReadingUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.IsBookItemReadingUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.UpdateBookStatusAndPercentageUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.UpdatePercentageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteBooksViewModel @Inject constructor(
    private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
    private val deleteFavoriteItemUseCase: DeleteFavoriteItemUseCase,
    private val addFavoriteItemUseCase: AddFavoriteItemUseCase,
    private val addReadingBookItemUseCase: AddReadingBookItemUseCase,
    private val deleteReadingBookItemUseCase: DeleteReadingBookItemUseCase,
    private val updatePercentageUseCase: UpdatePercentageUseCase,
    private val updateBookStatusAndPercentageUseCase: UpdateBookStatusAndPercentageUseCase,
    private val getBookItemReadingUseCase: GetBookItemReadingUseCase,
    private val isBookItemReadingUseCase: IsBookItemReadingUseCase
) : ViewModel() {

    private val _favoriteBooksScreenUiState = MutableLiveData<FavoriteBookListScreenUiState>()
    val favoriteBooksScreenUiState: LiveData<FavoriteBookListScreenUiState> get() = _favoriteBooksScreenUiState

    private lateinit var books: Flow<List<Book>>

    init {
        getFavoriteBooksFlowData()
    }

    private fun getFavoriteBooksFlowData() {
        books = getFavoriteItemsUseCase()
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

    fun addFavoriteItem(book: Book) {
        viewModelScope.launch {
            addFavoriteItemUseCase(book)
        }
    }

    fun addReadingBookItem(readingBook: ReadingBook) {
        viewModelScope.launch {
            addReadingBookItemUseCase(readingBook)
        }
    }

    fun deleteReadingBookItem(readingBook: ReadingBook) {
        viewModelScope.launch {
            deleteReadingBookItemUseCase(readingBook)
        }
    }

    fun getBookItemReading(itemId: String): LiveData<ReadingBook?> {
        return getBookItemReadingUseCase(itemId)
    }

    fun isBookItemReading(itemId: String): LiveData<Boolean> {
        return isBookItemReadingUseCase(itemId)
    }

    fun updatePercentage(bookId: Int, readingPercentage: Int, readingProcess: Int) {
        viewModelScope.launch {
            updatePercentageUseCase(bookId, readingPercentage, readingProcess)
        }
    }

    fun updateBookStatusAndPercentage(itemId: Int, readingStates: String, readingPercentage: Int) {
        viewModelScope.launch {
            updateBookStatusAndPercentageUseCase(itemId, readingStates, readingPercentage)
        }
    }
}