package com.android.moviesapp.enums

import android.content.Context
import com.android.moviesapp.R

enum class SortEnum(val columnName: String) {

    TITLE("title"),
    ADD_DATE("addDate"),
    VOTE_AVERAGE("voteAverage"),
    POPULARITY("popularity"),
    RELEASE_DATE("releaseDate"),
    VOTE_COUNT("voteCount"),
    ID("id");

    fun getSortName(context: Context): String =
        when(this) {
            TITLE -> context.getString(R.string.sort_title)
            ADD_DATE -> context.getString(R.string.sort_add_date)
            VOTE_AVERAGE -> context.getString(R.string.sort_vote_average)
            POPULARITY -> context.getString(R.string.sort_popularity)
            RELEASE_DATE -> context.getString(R.string.sort_release_date)
            VOTE_COUNT -> context.getString(R.string.sort_vote_count)
            ID -> context.getString(R.string.sort_id)
        }
}