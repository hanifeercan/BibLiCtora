package com.amineaytac.biblictora.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amineaytac.biblictora.core.database.entity.MyBooksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyBooksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFileItem(myBooksEntity: MyBooksEntity)

    @Query("SELECT * FROM myBooks_table")
    fun getAllFiles(): Flow<List<MyBooksEntity>>

    @Delete
    suspend fun deleteFileItem(myBooksEntity: MyBooksEntity)
}