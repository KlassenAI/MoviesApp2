package com.android.moviesapp.ui.movie

import androidx.lifecycle.ViewModel
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieRepository

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    fun insert(movie: Movie?) = repository.insert(movie)

    fun delete(movie: Movie?) = repository.delete(movie)
}