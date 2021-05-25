package com.android.moviesapp.ui.home

import androidx.lifecycle.ViewModel
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieRepository

class HomeMoviesViewModel(val repository: MovieRepository): ViewModel() {

    var popularMovies = repository.getPopularMovies()

    var upcomingMovies = repository.getUpcoming()

    var topRatedMovies = repository.getTopRated()

    fun insert(movie: Movie?) = repository.insert(movie)

    fun delete(movie: Movie?) = repository.delete(movie)
}