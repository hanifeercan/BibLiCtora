package com.amineaytac.biblictora.core.data.repo

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.amineaytac.biblictora.core.common.ResponseState
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import com.amineaytac.biblictora.core.database.source.LocalDataSource
import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import com.amineaytac.biblictora.core.network.source.paging.PagingSource
import com.amineaytac.biblictora.core.network.source.randomquote.RandomQuoteDataSource
import com.amineaytac.biblictora.core.network.source.rest.RestDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val restDataSource: RestDataSource,
    private val localDataSource: LocalDataSource,
    private val randomQuoteDataSource: RandomQuoteDataSource
) : BookRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getAllBooks(funcKey: String): Flow<PagingData<Book>> {
        val pagingSource = PagingSource(restDataSource, funcKey)
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { pagingSource }).flow
    }

    override suspend fun getBooksWithSearch(
        search: String, languages: List<String>, funcKey: String
    ): Flow<PagingData<Book>> {
        val pagingSource = PagingSource(restDataSource, funcKey, search, languages)
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { pagingSource }).flow
    }

    override suspend fun getBooksWithLanguages(
        languages: List<String>, funcKey: String
    ): Flow<PagingData<Book>> {
        val pagingSource = PagingSource(restDataSource, funcKey, languages = languages)
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { pagingSource }).flow
    }

    override fun getFavoriteItems(): Flow<List<Book>> {
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

    override fun getQuoteBook(bookId: Int): LiveData<QuotesEntity> {
        return localDataSource.getQuoteBook(bookId)
    }

    override fun getQuoteBooks(): Flow<List<QuoteBook>> {
        return localDataSource.getQuoteBooks()
            .map { it.map { quoteEntity -> quoteEntity.toQuoteBook() } }
    }

    override suspend fun addQuoteToBook(readingBook: ReadingBook, newQuote: String) {
        localDataSource.addQuoteToBook(readingBook, newQuote)
    }

    override suspend fun deleteQuoteFromBook(bookId: Int, quoteToRemove: String) {
        localDataSource.deleteQuoteFromBook(bookId, quoteToRemove)
    }

    override suspend fun updateQuotesList(bookId: Int, updatedList: List<QuoteItem>) {
        localDataSource.updateQuotesList(bookId, updatedList)
    }

    override fun getReadingBookItems(): Flow<List<ReadingBook>> {
        return localDataSource.getReadingBookItems()
            .map { it.map { readingStatusEntity -> readingStatusEntity.toReadingBook() } }
    }

    override suspend fun addReadingBookItem(readingBook: ReadingBook) {
        localDataSource.addReadingBookItem(readingBook.toStatusEntity())
    }

    override suspend fun deleteReadingBookItem(readingBook: ReadingBook) {
        localDataSource.deleteReadingBookItem(readingBook.toStatusEntity())
    }

    override suspend fun getRandomQuote(): StateFlow<ResponseState<QuoteResponse>> {
        return flow {
            emit(ResponseState.Loading)
            emit(ResponseState.Success(randomQuoteDataSource.getRandomQuote().toQuote()))
        }.catch {
            emit(ResponseState.Error(it.message.orEmpty()))
        }.stateIn(repositoryScope, SharingStarted.Lazily, ResponseState.Loading)
    }

    override suspend fun addFileItem(myBooksItem: MyBooksItem) {
        localDataSource.addFileItem(myBooksItem.toMyBooksEntity())
    }

    override fun getAllFiles(): Flow<List<MyBooksItem>> {
        return localDataSource.getAllFiles()
            .map { it.map { myBooksEntity -> myBooksEntity.toMyBooksItem() } }
    }

    override suspend fun deleteFileItem(myBooksItem: MyBooksItem) {
        localDataSource.deleteFileItem(myBooksItem.toMyBooksEntity())
    }

    override suspend fun getLastPage(filePath: String): Int {
        return localDataSource.getLastPage(filePath)
    }

    override suspend fun updateLastPage(filePath: String, lastPage: Int) {
        localDataSource.updateLastPage(filePath, lastPage)
    }

    override suspend fun getId(filePath: String): Int {
        return localDataSource.getId(filePath)
    }
}