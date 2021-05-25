package com.android.moviesapp.model

import androidx.room.TypeConverter
import java.lang.StringBuilder

class GenreIdsConverter {

    @TypeConverter
    fun fromListInt(input: List<Int>): String {
        val builder = StringBuilder()
        for (i in input) builder.append(i).append(" ")
        return builder.toString().trim()
    }

    @TypeConverter
    fun fromStringToListInt(input: String): List<Int> {
        val list = arrayListOf<Int>()
        val strings = input.split(" ")
        for (s in strings) list.add(Integer.parseInt(s))
        return list
    }
}