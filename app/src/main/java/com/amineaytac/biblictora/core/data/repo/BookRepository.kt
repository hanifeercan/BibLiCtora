package com.amineaytac.biblictora.core.data.repo

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.amineaytac.biblictora.core.common.ResponseState
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BookRepository {
    suspend fun getAllBooks(funcKey: String): Flow<PagingData<Book>>
    suspend fun getBooksWithSearch(
        search: String, languages: List<String>, funcKey: String
    ): Flow<PagingData<Book>>

    suspend fun getBooksWithLanguages(
        languages: List<String>, funcKey: String
    ): Flow<PagingData<Book>>

    suspend fun getFavoriteItems(): Flow<List<Book>>
    suspend fun addFavoriteItem(book: Book)
    suspend fun deleteFavoriteItem(book: Book)
    fun isItemFavorited(itemId: String): LiveData<Boolean>

    suspend fun getReadingBookItems(): Flow<List<ReadingBook>>

    suspend fun addReadingBookItem(readingBook: ReadingBook)

    suspend fun deleteReadingBookItem(readingBook: ReadingBook)

    fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity>

    fun isBookItemReading(itemId: String): LiveData<Boolean>

    fun getReadingPercentage(itemId: Int): LiveData<Int>

    suspend fun updateBookStatusAndPercentage(
        itemId: Int, readingStates: String, readingPercentage: Int
    )

    suspend fun updatePercentage(bookId: Int, readingPercentage: Int, readingProgress: Int)

    fun getQuoteBook(bookId: Int): LiveData<QuotesEntity>

    fun getQuoteBooks(): Flow<List<QuoteBook>>

    suspend fun addQuoteToBook(readingBook: ReadingBook, newQuote: String)

    suspend fun deleteQuoteFromBook(bookId: Int, quoteToRemove: String)

    suspend fun updateQuotesList(bookId: Int, updatedList: List<QuoteItem>)

    suspend fun getRandomQuote(): StateFlow<ResponseState<QuoteResponse>>

    suspend fun addFileItem(myBooksItem: MyBooksItem)
    suspend fun getAllFiles(): Flow<List<MyBooksItem>>
    suspend fun deleteFileItem(myBooksItem: MyBooksItem)
}