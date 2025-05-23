package com.amineaytac.biblictora.core.database.source

import androidx.lifecycle.LiveData
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.MyBooksEntity
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun addFavoriteItem(favoriteEntity: FavoriteEntity)
    suspend fun deleteFavoriteItem(favoriteEntity: FavoriteEntity)
    fun getFavoriteItems(): Flow<List<FavoriteEntity>>

    fun isItemFavorited(itemId: String): LiveData<Boolean>

    fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity>

    fun isBookItemReading(itemId: String): LiveData<Boolean>

    fun getReadingBookItems(): Flow<List<ReadingStatusEntity>>

    suspend fun addReadingBookItem(readingStatusEntity: ReadingStatusEntity)

    suspend fun deleteReadingBookItem(readingStatusEntity: ReadingStatusEntity)

    suspend fun updateBookStatusAndPercentage(
        itemId: Int, readingStates: String, readingPercentage: Int
    )

    suspend fun updatePercentage(bookId: Int, readingPercentage: Int, readingProgress: Int)

    fun getQuoteBook(bookId: Int): LiveData<QuotesEntity>

    fun getQuoteBooks(): Flow<List<QuotesEntity>>

    suspend fun addQuoteToBook(readingBook: ReadingBook, newQuote: String)

    suspend fun deleteQuoteFromBook(bookId: Int, quoteToRemove: String)

    suspend fun addFileItem(myBooksEntity: MyBooksEntity)

    fun getAllFiles(): Flow<List<MyBooksEntity>>

    suspend fun deleteFileItem(myBooksEntity: MyBooksEntity)

    suspend fun getLastPage(filePath: String): Int

    suspend fun updateLastPage(filePath: String, lastPage: Int)

    suspend fun getId(filePath: String): Int

}