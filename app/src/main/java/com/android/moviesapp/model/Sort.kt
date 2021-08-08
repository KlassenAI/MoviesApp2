package com.android.moviesapp.model

import com.android.moviesapp.model.Column.*

enum class Sort(private val column: Column, private val ascending: Boolean) {

    ADD_DATE_ASC(ADD_DATE, true),
    ADD_DATE_DESC(ADD_DATE, false),
    ID_ASC(ID, true),
    ID_DESC(ID, false),
    POPULARITY_ASC(POPULARITY, true),
    POPULARITY_DESC(POPULARITY, false),
    RELEASE_DATE_ASC(RELEASE_DATE, true),
    RELEASE_DATE_DESC(RELEASE_DATE, false),
    TITLE_ASC(TITLE, true),
    TITLE_DESC(TITLE, false),
    VOTE_AVERAGE_ASC(VOTE_AVERAGE, true),
    VOTE_AVERAGE_DESC(VOTE_AVERAGE, false),
    VOTE_COUNT_ASC(VOTE_COUNT, true),
    VOTE_COUNT_DESC(VOTE_COUNT, false);

    fun getColumn(): Column = column

    fun isAscending(): Boolean = ascending

    fun getSortWithName(): String {
        return "${column.columnName} ${if (ascending) "ASC" else "DESC"}"
    }
}