package com.amineaytac.biblictora.core.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.amineaytac.biblictora.core.data.model.ReadFormats
import com.google.gson.Gson

@TypeConverters
class TypeConverter {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromReadFormats(readFormats: ReadFormats): String {
        return Gson().toJson(readFormats)
    }

    @TypeConverter
    fun toReadFormats(value: String): ReadFormats {
        return Gson().fromJson(value, ReadFormats::class.java)
    }
}