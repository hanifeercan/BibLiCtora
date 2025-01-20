package com.amineaytac.biblictora.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.BookRepository
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import com.amineaytac.biblictora.core.domain.favorite.AddFavoriteItemUseCase
import com.amineaytac.biblictora.core.domain.favorite.DeleteFavoriteItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.AddReadingBookItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.DeleteReadingBookItemUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.UpdateBookStatusAndPercentageUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.UpdatePercentageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val addFavoriteItemUseCase: AddFavoriteItemUseCase,
    private val deleteFavoriteItemUseCase: DeleteFavoriteItemUseCase,
    private val bookRepository: BookRepository,
    private val addReadingBookItemUseCase: AddReadingBookItemUseCase,
    private val deleteReadingBookItemUseCase: DeleteReadingBookItemUseCase,
    private val updatePercentageUseCase: UpdatePercentageUseCase,
    private val updateBookStatusAndPercentageUseCase: UpdateBookStatusAndPercentageUseCase

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

    fun isItemFavorited(itemId: String): LiveData<Boolean> {
        return bookRepository.isItemFavorited(itemId)
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

    fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity> {
        return bookRepository.getBookItemReading(itemId)
    }

    fun isBookItemReading(itemId: String): LiveData<Boolean> {
        return bookRepository.isBookItemReading(itemId)
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