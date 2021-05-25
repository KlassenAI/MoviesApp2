package com.android.moviesapp.model

import com.android.moviesapp.util.Constants

open class GenreIdsBindingAdapter {
    companion object {
        @JvmStatic
        fun fromGenreIds(list: List<Int?>): String {
            val map = Constants.genreMap

            var count = 0
            val maxCount = 3
            val stringBuilder = StringBuilder()
            for (i in list) {
                stringBuilder.append(map[i]).append(" ")
                count++
                if (count == maxCount) break
            }
            return stringBuilder.toString().trim()
        }
    }
}