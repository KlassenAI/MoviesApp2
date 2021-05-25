package com.android.moviesapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieCallback
import com.android.moviesapp.repository.MovieRepository

class HomeViewModel(private val repository: MovieRepository): ViewModel() {
    val popularMovies = MutableLiveData<List<Movie>>()
    val upcomingMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies = MutableLiveData<List<Movie>>()

    fun getPopularMovies() {
        repository.requestPopularMovies(object : MovieCallback {
            override fun onResponse(movies: List<Movie>) { popularMovies.postValue(movies) }
            override fun onFailure(t: Throwable) {}
        })
    }

    fun getUpcomingMovies() {
        repository.requestUpcomingMovies(object : MovieCallback {
            override fun onResponse(movies: List<Movie>) { upcomingMovies.postValue(movies) }
            override fun onFailure(t: Throwable) {}
        })
    }

    fun getTopRatedMovies() {
        repository.requestTopRatedMovies(object : MovieCallback {
            override fun onResponse(movies: List<Movie>) { topRatedMovies.postValue(movies) }
            override fun onFailure(t: Throwable) {}
        })
    }
}