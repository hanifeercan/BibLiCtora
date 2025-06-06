package com.amineaytac.biblictora.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.domain.favorite.AddFavoriteItemUseCase
import com.amineaytac.biblictora.core.domain.favorite.DeleteFavoriteItemUseCase
import com.amineaytac.biblictora.core.domain.favorite.IsItemFavoriteUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.AddReadingBookItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.DeleteReadingBookItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.GetBookItemReadingUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.IsBookItemReadingUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.UpdateBookStatusAndPercentageUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.UpdatePercentageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val addFavoriteItemUseCase: AddFavoriteItemUseCase,
    private val deleteFavoriteItemUseCase: DeleteFavoriteItemUseCase,
    private val addReadingBookItemUseCase: AddReadingBookItemUseCase,
    private val deleteReadingBookItemUseCase: DeleteReadingBookItemUseCase,
    private val updatePercentageUseCase: UpdatePercentageUseCase,
    private val updateBookStatusAndPercentageUseCase: UpdateBookStatusAndPercentageUseCase,
    private val getBookItemReadingUseCase: GetBookItemReadingUseCase,
    private val isBookItemReadingUseCase: IsBookItemReadingUseCase,
    private val isItemFavoriteUseCase: IsItemFavoriteUseCase
) : ViewModel() {
    fun addFavoriteItem(book: Book) {
        viewModelScope.launch {
            addFavoriteItemUseCase(book)
        }
    }

    fun deleteFavoriteItem(book: Book) {
        viewModelScope.launch {
            deleteFavoriteItemUseCase(book)
        }
    }

    fun isItemFavorite(itemId: String): LiveData<Boolean> {
        return isItemFavoriteUseCase(itemId)
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