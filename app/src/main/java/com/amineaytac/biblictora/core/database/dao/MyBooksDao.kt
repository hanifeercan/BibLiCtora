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

    @Query("SELECT lastPage FROM myBooks_table WHERE filePath = :filePath LIMIT 1")
    suspend fun getLastPage(filePath: String): Int

    @Query("UPDATE myBooks_table SET lastPage = :lastPage WHERE filePath = :filePath")
    suspend fun updateLastPage(filePath: String, lastPage: Int)

    @Query("SELECT id FROM myBooks_table WHERE filePath = :filePath LIMIT 1")
    suspend fun getId(filePath: String): Int
}