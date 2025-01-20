package com.amineaytac.biblictora.core.database.source

import androidx.lifecycle.LiveData
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun addFavoriteItem(favoriteEntity: FavoriteEntity)
    suspend fun deleteFavoriteItem(favoriteEntity: FavoriteEntity)
    suspend fun getFavoriteItems(): Flow<List<FavoriteEntity>>

    fun isItemFavorited(itemId: String): LiveData<Boolean>

    fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity>

    fun isBookItemReading(itemId: String): LiveData<Boolean>

    fun getReadingPercentage(itemId: Int): LiveData<Int>

    suspend fun getReadingBookItems(): Flow<List<ReadingStatusEntity>>

    suspend fun addReadingBookItem(readingStatusEntity: ReadingStatusEntity)

    suspend fun deleteReadingBookItem(readingStatusEntity: ReadingStatusEntity)

    suspend fun updateBookStatusAndPercentage(
        itemId: Int, readingStates: String, readingPercentage: Int
    )

    suspend fun updatePercentage(bookId: Int, readingPercentage: Int, readingProgress: Int)
}