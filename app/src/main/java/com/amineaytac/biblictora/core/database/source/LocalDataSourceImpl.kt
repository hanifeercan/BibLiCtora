package com.amineaytac.biblictora.core.database.source

import androidx.lifecycle.LiveData
import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.dao.FavoriteDao
import com.amineaytac.biblictora.core.database.dao.MyBooksDao
import com.amineaytac.biblictora.core.database.dao.QuotesDao
import com.amineaytac.biblictora.core.database.dao.ReadingStatusDao
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.MyBooksEntity
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val readingStatusDao: ReadingStatusDao,
    private val quotesDao: QuotesDao,
    private val myBooksDao: MyBooksDao
) : LocalDataSource {

    override suspend fun addFavoriteItem(favoriteEntity: FavoriteEntity) {
        favoriteDao.addFavoriteItem((favoriteEntity))
    }

    override suspend fun deleteFavoriteItem(favoriteEntity: FavoriteEntity) {
        favoriteDao.deleteFavoriteItem(favoriteEntity)
    }

    override fun getFavoriteItems(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getFavoriteItems()
    }

    override fun isItemFavorited(itemId: String): LiveData<Boolean> {
        return favoriteDao.isItemFavorited(itemId)
    }

    override fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity> {
        return readingStatusDao.getBookItemReading(itemId)
    }

    override fun isBookItemReading(itemId: String): LiveData<Boolean> {
        return readingStatusDao.isBookItemReading(itemId)
    }

    override fun getReadingPercentage(itemId: Int): LiveData<Int> {
        return readingStatusDao.getReadingPercentage(itemId)
    }

    override fun getReadingBookItems(): Flow<List<ReadingStatusEntity>> {
        return readingStatusDao.getReadingBookItems()
    }

    override suspend fun addReadingBookItem(readingStatusEntity: ReadingStatusEntity) {
        readingStatusDao.addReadingBookItem(readingStatusEntity)
    }

    override suspend fun deleteReadingBookItem(readingStatusEntity: ReadingStatusEntity) {
        readingStatusDao.deleteReadingBookItem(readingStatusEntity)
    }

    override suspend fun updateBookStatusAndPercentage(
        itemId: Int, readingStates: String, readingPercentage: Int
    ) {
        readingStatusDao.updateBookStatusAndPercentage(itemId, readingStates, readingPercentage)
    }

    override suspend fun updatePercentage(
        bookId: Int, readingPercentage: Int, readingProgress: Int
    ) {
        readingStatusDao.updatePercentage(bookId, readingPercentage, readingProgress)
    }

    override fun getQuoteBook(bookId: Int): LiveData<QuotesEntity> {
        return quotesDao.getQuoteBook(bookId)
    }

    override fun getQuoteBooks(): Flow<List<QuotesEntity>> {
        return quotesDao.getQuoteBooks()
    }

    override suspend fun addQuoteToBook(readingBook: ReadingBook, newQuote: String) {
        quotesDao.addQuoteToBook(readingBook, newQuote)
    }

    override suspend fun deleteQuoteFromBook(bookId: Int, quoteToRemove: String) {
        quotesDao.deleteQuoteFromBook(bookId, quoteToRemove)
    }

    override suspend fun updateQuotesList(bookId: Int, updatedList: List<QuoteItem>) {
        quotesDao.updateQuotesList(bookId, updatedList)
    }

    override suspend fun addFileItem(myBooksEntity: MyBooksEntity) {
        myBooksDao.addFileItem(myBooksEntity)
    }

    override fun getAllFiles(): Flow<List<MyBooksEntity>> {
        return myBooksDao.getAllFiles()
    }

    override suspend fun deleteFileItem(myBooksEntity: MyBooksEntity) {
        myBooksDao.deleteFileItem(myBooksEntity)
    }

    override suspend fun getLastPage(filePath: String): Int {
        return myBooksDao.getLastPage(filePath)
    }

    override suspend fun updateLastPage(filePath: String, lastPage: Int) {
        myBooksDao.updateLastPage(filePath, lastPage)
    }

    override suspend fun getId(filePath: String): Int {
        return myBooksDao.getId(filePath)
    }
}