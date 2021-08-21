package com.android.moviesapp.util

import androidx.appcompat.app.ActionBar
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.ConcatAdapter
import com.android.moviesapp.adapter.PagingListItemAdapter

class Expansions {

    companion object {

        fun ActionBar.setHomeBtn(showHome: Boolean) {
            setDisplayHomeAsUpEnabled(showHome)
            setDisplayShowHomeEnabled(showHome)
        }

        fun PagingListItemAdapter.withLoadStateHeaderAndFooter(loadStateAdapter: LoadStateAdapter<*>): ConcatAdapter =
            this.withLoadStateHeaderAndFooter(loadStateAdapter, loadStateAdapter)
    }
}