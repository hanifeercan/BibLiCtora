package com.amineaytac.biblictora.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateBook(quotesEntity: QuotesEntity)

    @Query("SELECT quotes_list FROM quotes_table WHERE id = :bookId")
    suspend fun getQuotesForBook(bookId: Int): List<String>?

    @Query("SELECT * FROM quotes_table WHERE id = :bookId")
    fun getQuoteBook(bookId: Int): LiveData<QuotesEntity>

    @Query("SELECT * FROM quotes_table")
    fun getQuoteBooks(): Flow<List<QuotesEntity>>

    @Transaction
    suspend fun addQuoteToBook(readingBook: ReadingBook, newQuote: String) {
        val currentQuotes = getQuotesForBook(readingBook.id)

        if (currentQuotes == null) {
            val newQuotesEntity = QuotesEntity(
                id = readingBook.id,
                title = readingBook.title,
                authors = readingBook.authors,
                image = readingBook.image,
                quotesList = listOf(newQuote)
            )
            addOrUpdateBook(newQuotesEntity)
        } else {
            val updatedList = currentQuotes.toMutableList().apply {
                add(newQuote)
            }

            val updatedBook = QuotesEntity(
                id = readingBook.id,
                title = readingBook.title,
                authors = readingBook.authors,
                image = readingBook.image,
                quotesList = updatedList
            )
            addOrUpdateBook(updatedBook)
        }
    }

    @Transaction
    suspend fun deleteQuoteFromBook(bookId: Int, quoteToRemove: String) {
        val currentQuotes = getQuotesForBook(bookId)
        currentQuotes?.let {
            val updatedList = it.filter { quote -> quote != quoteToRemove }
            updateQuotesList(bookId, updatedList)
        }
    }

    @Query("UPDATE quotes_table SET quotes_list = :updatedList WHERE id = :bookId")
    suspend fun updateQuotesList(bookId: Int, updatedList: List<String>) {
        if (updatedList.isEmpty()) {
            deleteBookById(bookId)
        } else {
            updateQuotesList(bookId, updatedList)
        }
    }

    @Query("DELETE FROM quotes_table WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Int)
}