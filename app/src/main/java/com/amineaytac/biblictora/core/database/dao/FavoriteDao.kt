package com.amineaytac.biblictora.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavoriteItem(favoriteEntity: FavoriteEntity)

    @Delete
    suspend fun deleteFavoriteItem(favoriteEntity: FavoriteEntity)

    @Query("SELECT * FROM favorite_table")
    fun getFavoriteItems(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_table WHERE id = :itemId LIMIT 1)")
    fun isItemFavorite(itemId: String): LiveData<Boolean>
}