package com.amineaytac.biblictora.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateBook(quotesEntity: QuotesEntity)

    @Query("SELECT quotes_list FROM quotes_table WHERE id = :bookId")
    suspend fun getQuotesForBook(bookId: Int): String?

    @Query("SELECT * FROM quotes_table WHERE id = :bookId")
    fun getQuoteBook(bookId: Int): LiveData<QuotesEntity>

    @Query("SELECT * FROM quotes_table")
    fun getQuoteBooks(): Flow<List<QuotesEntity>>

    suspend fun addQuoteToBook(readingBook: ReadingBook, newQuoteText: String) {
        val currentQuotesJson = getQuotesForBook(readingBook.id)

        val currentQuotes = currentQuotesJson?.let {
            val type = object : TypeToken<List<QuoteItem>>() {}.type
            try {
                Gson().fromJson<List<QuoteItem>>(it, type)
            } catch (e: JsonSyntaxException) {
                null
            }
        } ?: emptyList()

        val updatedQuotes = currentQuotes.toMutableList()
        val newQuote = QuoteItem(id = updatedQuotes.size, quote = newQuoteText)

        if (updatedQuotes.none {
                it.quote.trim().equals(newQuote.quote.trim(), ignoreCase = true)
            }) {
            updatedQuotes.add(newQuote)
        }

        val updatedBook = QuotesEntity(
            id = readingBook.id,
            title = readingBook.title,
            authors = readingBook.authors,
            image = readingBook.image,
            quotesList = Gson().toJson(updatedQuotes)
        )
        addOrUpdateBook(updatedBook)
    }

    @Transaction
    suspend fun deleteQuoteFromBook(bookId: Int, quoteToRemove: String) {

        val currentQuotesJson = getQuotesForBook(bookId)
        val currentQuotes: List<QuoteItem>? = currentQuotesJson?.let {
            Gson().fromJson(it, object : TypeToken<List<QuoteItem>>() {}.type)
        }

        if (currentQuotes == null || currentQuotes.isEmpty()) {
            return
        }

        val updatedList = currentQuotes.filter { quote ->
            val isKept = !quote.quote.trim().equals(quoteToRemove.trim(), ignoreCase = true)
            isKept
        }

        if (updatedList.isEmpty()) {
            deleteBookById(bookId)
        } else {
            updateQuotesList(bookId, updatedList)
        }
    }

    @Transaction
    suspend fun updateQuotesList(bookId: Int, updatedList: List<QuoteItem>) {

        val updatedListJson = Gson().toJson(updatedList)
        updateQuotesListJson(bookId, updatedListJson)
    }

    @Query("UPDATE quotes_table SET quotes_list = :updatedListJson WHERE id = :bookId")
    suspend fun updateQuotesListJson(bookId: Int, updatedListJson: String)

    @Query("DELETE FROM quotes_table WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Int)
}