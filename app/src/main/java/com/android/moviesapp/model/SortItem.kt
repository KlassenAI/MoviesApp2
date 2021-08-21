package com.android.moviesapp.model

import com.android.moviesapp.enums.SortEnum

data class SortItem(
    var sortEnum: SortEnum = SortEnum.ADD_DATE,
    var ascending: Boolean = false
) {
    fun reverseOrder() {
        ascending = ascending.not()
    }

    fun getColumnName() = sortEnum.columnName

    fun getColumnAscending() = if (ascending) "ASC" else "DESC"
}