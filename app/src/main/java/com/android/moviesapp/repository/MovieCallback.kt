package com.android.moviesapp.repository

import com.android.moviesapp.model.Movie

interface MovieCallback {
    fun onResponse(movies: List<Movie>)
    fun onFailure(t: Throwable)
}