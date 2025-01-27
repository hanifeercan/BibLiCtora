package com.amineaytac.biblictora.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes_table")
data class QuotesEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "authors")
    val authors: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "quotes_list")
    val quotesList: List<String>
)