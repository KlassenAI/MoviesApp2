package com.android.moviesapp.callback

import com.android.moviesapp.model.SortItem

interface WatchListDialogCallback {
    fun setSortList(sortItem: SortItem)
}
