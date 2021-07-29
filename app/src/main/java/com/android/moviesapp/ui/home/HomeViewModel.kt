package com.android.moviesapp.ui.home

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.android.moviesapp.ui.main.MainViewModel

open class HomeViewModel(application: Application) : MainViewModel(application) {

    companion object {
        private const val DEFAULT_QUERY = "query"
        private const val DEFAULT_GENRE = "0"
    }

    private val defaultQuery = MutableLiveData("")

    val popularMovies = defaultQuery.switchMap {
        repository.getPopular().cachedIn(viewModelScope)
    }

    val topMovies = defaultQuery.switchMap {
        repository.getTop().cachedIn(viewModelScope)
    }

    val upcomingMovies = defaultQuery.switchMap {
        repository.getUpcoming().cachedIn(viewModelScope)
    }

    fun getMovies() {
        defaultQuery.value = DEFAULT_QUERY
    }

    private val currentGenre = MutableLiveData(DEFAULT_GENRE)

    val genreMovies = currentGenre.switchMap {
        repository.getByGenre(it).cachedIn(viewModelScope)
    }

    fun getGenreMovies(genre: String) {
        currentGenre.value = genre
    }
}
