package com.android.moviesapp.ui.search

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieRepository

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {
    private val currentQuery = MutableLiveData(DEFAULT_QUERY)
    var searchMovies = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope) }

    companion object {
        private const val DEFAULT_QUERY = ""
    }

    fun getSearchMovies(query: String) { currentQuery.value = query }

    fun insert(movie: Movie?) = repository.insert(movie)

    fun delete(movie: Movie?) = repository.delete(movie)
}