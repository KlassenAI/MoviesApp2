package com.android.moviesapp.callback

import com.android.moviesapp.model.Movie

interface AdapterCallback {
    fun showMovieInfo(movie: Movie)
}
