package com.amineaytac.biblictora.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amineaytac.biblictora.core.data.model.ReadFormats

@Entity(tableName = "reading_status_table")
data class ReadingStatusEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "authors") val authors: String,
    @ColumnInfo(name = "bookshelves") val bookshelves: List<String>,
    @ColumnInfo(name = "languages") val languages: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "formats") val formats: ReadFormats,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "readingStates") val readingStates: String,
    @ColumnInfo(name = "readingPercentage") val readingPercentage: Int,
    @ColumnInfo(name = "readingProgress") val readingProgress: Int
)