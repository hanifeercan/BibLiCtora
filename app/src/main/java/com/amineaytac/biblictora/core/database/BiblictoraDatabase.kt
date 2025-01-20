package com.amineaytac.biblictora.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amineaytac.biblictora.core.database.dao.FavoriteDao
import com.amineaytac.biblictora.core.database.dao.ReadingStatusDao
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity

@Database(entities = [FavoriteEntity::class, ReadingStatusEntity::class], version = 6)
@TypeConverters(TypeConverter::class)
abstract class BiblictoraDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun readingStatusDao(): ReadingStatusDao
}