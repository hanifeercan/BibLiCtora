package com.amineaytac.biblictora.core.data.repo

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import com.amineaytac.biblictora.core.database.source.LocalDataSource
import com.amineaytac.biblictora.core.network.source.paging.PagingSource
import com.amineaytac.biblictora.core.network.source.rest.RestDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val restDataSource: RestDataSource, private val localDataSource: LocalDataSource
) : BookRepository {
    override suspend fun getAllBooks(funcKey: String): Flow<PagingData<Book>> {
        val pagingSource = PagingSource(restDataSource, funcKey)
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { pagingSource }).flow
    }

    override suspend fun getBooksWithSearch(
        search: String, languages: List<String>, funcKey: String
    ): Flow<PagingData<Book>> {
        val pagingSource = PagingSource(restDataSource, funcKey, search, languages)
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { pagingSource }).flow
    }

    override suspend fun getBooksWithLanguages(
        languages: List<String>, funcKey: String
    ): Flow<PagingData<Book>> {
        val pagingSource = PagingSource(restDataSource, funcKey, languages = languages)
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { pagingSource }).flow
    }

    override suspend fun getFavoriteItems(): Flow<List<Book>> {
        return localDataSource.getFavoriteItems()
            .map { it.map { favoriteEntity -> favoriteEntity.toBook() } }
    }

    override suspend fun addFavoriteItem(book: Book) {
        localDataSource.addFavoriteItem(book.toFavoriteItemEntity())
    }

    override suspend fun deleteFavoriteItem(book: Book) {
        localDataSource.deleteFavoriteItem(book.toFavoriteItemEntity())
    }

    override fun isItemFavorited(itemId: String): LiveData<Boolean> {
        return localDataSource.isItemFavorited(itemId)
    }

    override fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity> {
        return localDataSource.getBookItemReading(itemId)
    }

    override fun isBookItemReading(itemId: String): LiveData<Boolean> {
        return localDataSource.isBookItemReading(itemId)
    }

    override fun getReadingPercentage(itemId: Int): LiveData<Int> {
        return localDataSource.getReadingPercentage(itemId)
    }

    override suspend fun updateBookStatusAndPercentage(
        itemId: Int, readingStates: String, readingPercentage: Int
    ) {
        localDataSource.updateBookStatusAndPercentage(itemId, readingStates, readingPercentage)
    }

    override suspend fun updatePercentage(
        bookId: Int, readingPercentage: Int, readingProgress: Int
    ) {
        localDataSource.updatePercentage(bookId, readingPercentage, readingProgress)
    }

    override suspend fun getReadingBookItems(): Flow<List<ReadingBook>> {
        return localDataSource.getReadingBookItems()
            .map { it.map { readingStatusEntity -> readingStatusEntity.toReadingBook() } }
    }

    override suspend fun addReadingBookItem(readingBook: ReadingBook) {
        localDataSource.addReadingBookItem(readingBook.toStatusEntity())
    }

    override suspend fun deleteReadingBookItem(readingBook: ReadingBook) {
        localDataSource.deleteReadingBookItem(readingBook.toStatusEntity())
    }
}