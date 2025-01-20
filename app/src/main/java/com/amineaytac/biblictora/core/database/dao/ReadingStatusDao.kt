package com.amineaytac.biblictora.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingStatusDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReadingBookItem(readingStatusEntity: ReadingStatusEntity)

    @Delete
    suspend fun deleteReadingBookItem(readingStatusEntity: ReadingStatusEntity)

    @Query("SELECT * FROM reading_status_table")
    fun getReadingBookItems(): Flow<List<ReadingStatusEntity>>

    @Query("SELECT * FROM reading_status_table WHERE id = :itemId LIMIT 1")
    fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM reading_status_table WHERE id = :itemId LIMIT 1)")
    fun isBookItemReading(itemId: String): LiveData<Boolean>

    @Query("SELECT readingPercentage FROM reading_status_table WHERE id = :itemId LIMIT 1")
    fun getReadingPercentage(itemId: Int): LiveData<Int>

    @Query("UPDATE reading_status_table SET readingStates = :readingStates, readingPercentage = :readingPercentage WHERE id = :itemId")
    suspend fun updateBookStatusAndPercentage(
        itemId: Int, readingStates: String, readingPercentage: Int
    )

    @Query("UPDATE reading_status_table SET readingPercentage = :readingPercentage, readingProgress = :readingProgress WHERE id = :bookId")
    suspend fun updatePercentage(
        bookId: Int, readingPercentage: Int, readingProgress: Int
    )
}