package com.amineaytac.biblictora.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "myBooks_table")
data class MyBooksEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val filePath: String,
    val fileType: String,
    val lastPage: Int = 0
)