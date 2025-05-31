package com.amineaytac.biblictora.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amineaytac.biblictora.core.database.dao.FavoriteDao
import com.amineaytac.biblictora.core.database.dao.MyBooksDao
import com.amineaytac.biblictora.core.database.dao.QuotesDao
import com.amineaytac.biblictora.core.database.dao.ReadingStatusDao
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.MyBooksEntity
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity

@Database(
    entities = [
        FavoriteEntity::class,
        ReadingStatusEntity::class,
        QuotesEntity::class,
        MyBooksEntity::class
    ],
    version = 9, exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class BiblictoraDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun readingStatusDao(): ReadingStatusDao
    abstract fun quotesDao(): QuotesDao

    abstract fun myBooksDao(): MyBooksDao
}