package com.amineaytac.biblictora.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amineaytac.biblictora.core.data.model.ReadFormats

@Entity(tableName = "favorite_table")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "authors")
    val authors: String,
    @ColumnInfo(name = "bookshelves")
    val bookshelves: List<String>,
    @ColumnInfo(name = "languages")
    val languages: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "formats")
    val formats: ReadFormats,
    @ColumnInfo(name = "image")
    val image: String
)