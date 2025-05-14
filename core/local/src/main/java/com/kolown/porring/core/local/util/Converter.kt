package com.kolown.porring.core.local.util

import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun String.toStringList() = if (this.isEmpty()) emptyList() else this.split(",")

    @TypeConverter
    fun List<String>.stringToData() = this.joinToString(",")

    @TypeConverter
    fun String.toIntList() = if (this.isEmpty()) emptyList() else this.split(",").map { it.toInt() }

    @TypeConverter
    fun List<Int>.intToData() = this.joinToString(",")
}