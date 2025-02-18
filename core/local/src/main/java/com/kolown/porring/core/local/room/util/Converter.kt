package com.kolown.porring.core.local.room.util

import androidx.room.TypeConverter
import com.kolown.porring.core.local.room.entity.PostType

class Converter {
    @TypeConverter
    fun String.toStringList() = if (this.isEmpty()) emptyList() else this.split(",")

    @TypeConverter
    fun List<String>.stringToData() = this.joinToString(",")

    @TypeConverter
    fun String.toIntList() = if (this.isEmpty()) emptyList() else this.split(",").map { it.toInt() }

    @TypeConverter
    fun List<Int>.intToData() = this.joinToString(",")

    @TypeConverter
    fun PostType.fromPostType() = this.name

    @TypeConverter
    fun String.toPostType() = PostType.valueOf(this)
}