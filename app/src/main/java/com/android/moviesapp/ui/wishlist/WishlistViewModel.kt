package com.android.moviesapp.ui.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.moviesapp.model.ItemMovie
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieRepository

class WishlistViewModel(private val repository: MovieRepository) : ViewModel() {

    val favoritesMovies: LiveData<List<Movie>> = repository.getFavoriteMovies()

    fun insert(movie: Movie?) = repository.insert(movie)

    fun delete(movie: Movie?) = repository.delete(movie)
}