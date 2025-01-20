package com.amineaytac.biblictora.core.database.source

import androidx.lifecycle.LiveData
import com.amineaytac.biblictora.core.database.dao.FavoriteDao
import com.amineaytac.biblictora.core.database.dao.ReadingStatusDao
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao, private val readingStatusDao: ReadingStatusDao
) : LocalDataSource {

    override suspend fun addFavoriteItem(favoriteEntity: FavoriteEntity) {
        favoriteDao.addFavoriteItem((favoriteEntity))
    }

    override suspend fun deleteFavoriteItem(favoriteEntity: FavoriteEntity) {
        favoriteDao.deleteFavoriteItem(favoriteEntity)
    }

    override suspend fun getFavoriteItems(): Flow<List<FavoriteEntity>> {
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

    override suspend fun getReadingBookItems(): Flow<List<ReadingStatusEntity>> {
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
        bookId: Int,
        readingPercentage: Int,
        readingProgress: Int
    ) {
        readingStatusDao.updatePercentage(bookId, readingPercentage, readingProgress)
    }
}