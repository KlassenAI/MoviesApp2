package com.android.moviesapp.util

import androidx.collection.arrayMapOf
import java.util.*

object Constants {

    val genreMap: Map<Int, String>
        get() {
            val genreMap = arrayMapOf<Int, String>()
            genreMap[28] = "Боевик"
            genreMap[12] = "Приключения"
            genreMap[16] = "Мультфильм"
            genreMap[35] = "Комедия"
            genreMap[80] = "Криминал"
            genreMap[99] = "Документальный"
            genreMap[18] = "Драма"
            genreMap[10751] = "Семейный"
            genreMap[14] = "Фэнтези"
            genreMap[36] = "Исторический"
            genreMap[27] = "Ужасы"
            genreMap[10402] = "Музыка"
            genreMap[9648] = "Детектив"
            genreMap[10749] = "Мелодрама"
            genreMap[878] = "Фантастика"
            genreMap[53] = "Триллер"
            genreMap[10752] = "Военный"
            genreMap[37] = "Вестерн"
            genreMap[10770] = "Телевизионный фильм"
            return genreMap
        }

    val genreArray: ArrayList<String>
    get() {
        val genreArray = arrayListOf<String>()
        genreArray.run {
            add("Боевик")
            add("Приключения")
            add("Мультфильм")
            add("Комедия")
            add("Криминал")
            add("Документальный")
            add("Драма")
            add("Семейный")
            add("Фэнтези")
            add("Исторический")
            add("Ужасы")
            add("Музыка")
            add("Детектив")
            add("Мелодрама")
            add("Фантастика")
            add("Триллер")
            add("Военный")
            add("Вестерн")
            add("Телевизионный фильм")
        }
        return genreArray
    }

    val genreIdsArray: ArrayList<Int>
    get() {
        val genreIdsArray = arrayListOf<Int>()
        genreIdsArray.run {
            add(28)
            add(12)
            add(16)
            add(35)
            add(80)
            add(99)
            add(18)
            add(10751)
            add(14)
            add(36)
            add(27)
            add(10402)
            add(9648)
            add(10749)
            add(878)
            add(53)
            add(10752)
            add(37)
            add(10770)
        }
        return genreIdsArray
    }

    fun getKey(position: Int): Int {
        return genreIdsArray[position]
    }
}