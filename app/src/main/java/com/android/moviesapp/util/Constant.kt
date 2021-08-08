package com.android.moviesapp.util

import android.content.Context
import com.android.moviesapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Constant {

    companion object {

        const val TABLE_NAME = "movie_table"
        const val DATABASE_NAME = "movie_database"
        const val API_KEY = "bbc469558822b67257a0bd5a0534463e"
        const val LANG_RU = "ru"
        const val REG_RU = "ru"

        const val BUNDLE_KEY_GENRE = "genre"

        const val KEY_API_KEY = "api_key"
        const val KEY_LANG = "language"
        const val KEY_REGION = "region"
        const val KEY_QUERY = "query"
        const val KEY_PAGE = "page"
        const val KEY_GENRE = "with_genres"
        const val KEY_COUNTRY = "with_original_language"
        const val KEY_SORT_BY = "sort_by"
        const val KEY_YEAR = "year"
        const val KEY_RELEASE_DATE_LESS = "release_date.lte"
        const val KEY_RELEASE_DATE_MORE = "release_date.gte"
        const val KEY_VOTE_AVERAGE = "vote_average.gte"
        const val KEY_VOTE_COUNT = "vote_count.gte"
        const val KEY_RUNTIME_LESS = "with_runtime.lte"
        const val KEY_RUNTIME_MORE = "with_runtime.gte"

        fun getRateList(context: Context): List<String> =
            listOf(
                context.getString(R.string.rate_8),
                context.getString(R.string.rate_7),
                context.getString(R.string.rate_6),
            )

        fun getCountList(context: Context): List<String> =
            listOf(
                context.getString(R.string.count_10000),
                context.getString(R.string.count_1000),
                context.getString(R.string.count_100),
            )

        fun getRuntimeList(context: Context): List<String> =
            listOf(
                context.getString(R.string.less_hour),
                context.getString(R.string.one_two_hours),
                context.getString(R.string.two_three_hours),
                context.getString(R.string.more_three_hours)
            )

        private fun getGenreMap(context: Context): HashMap<Int, String> =
            hashMapOf(
                12 to context.getString(R.string.adventure),
                14 to context.getString(R.string.fantasy),
                16 to context.getString(R.string.animation),
                18 to context.getString(R.string.drama),
                27 to context.getString(R.string.horror),
                28 to context.getString(R.string.action),
                35 to context.getString(R.string.comedy),
                36 to context.getString(R.string.history),
                37 to context.getString(R.string.western),
                53 to context.getString(R.string.thriller),
                80 to context.getString(R.string.crime),
                99 to context.getString(R.string.documentary),
                878 to context.getString(R.string.science_fiction),
                9648 to context.getString(R.string.mystery),
                10402 to context.getString(R.string.music),
                10749 to context.getString(R.string.romance),
                10751 to context.getString(R.string.family),
                10752 to context.getString(R.string.war),
                10770 to context.getString(R.string.tv_movie),
            )

        fun getNamesAllGenres(context: Context): List<String> {
            val strings = getGenreMap(context).values
            val array = arrayListOf<String>()
            array.addAll(strings)
            array.sort()
            return array
        }

        fun getSortList(context: Context): List<String> =
            listOf(
                context.getString(R.string.sort_popularity),
                context.getString(R.string.sort_release_date),
                context.getString(R.string.sort_vote_average),
                context.getString(R.string.sort_vote_count),
            )

        fun getSortValue(context: Context, sort: String, ascending: Boolean): String? {
            val sortList = getSortList(context)
            return when(sort) {
                sortList[0] -> {
                    if (ascending) context.getString(R.string.sort_popularity_asc)
                    else context.getString(R.string.sort_popularity_desc)
                }
                sortList[1] -> {
                    if (ascending) context.getString(R.string.sort_release_date_asc)
                    else context.getString(R.string.sort_release_date_desc)
                }
                sortList[2] -> {
                    if (ascending) context.getString(R.string.sort_vote_average_asc)
                    else context.getString(R.string.sort_vote_average_desc)
                }
                sortList[3] -> {
                    if (ascending) context.getString(R.string.sort_vote_count_asc)
                    else context.getString(R.string.sort_popularity_desc)
                }
                else -> null
            }
        }

        fun getIdByGenre(context: Context, genre: String): String =
            getGenreMap(context).filterValues { it == genre.toLowerCase(Locale.ROOT) }.keys.first().toString()

        fun getGenreById(context: Context, id: Int): String =
            getGenreMap(context)[id]!!

    }
}