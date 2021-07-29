package com.android.moviesapp.ui.search

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.android.moviesapp.ui.main.MainViewModel

class SearchViewModel(application: Application) : MainViewModel(application) {

    companion object {
        private const val DEFAULT_QUERY = ""
    }

    val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val searchMovies = currentQuery.switchMap {
        repository.getSearchMovies(it).cachedIn(viewModelScope)
    }

    fun searchMovies(query: String) {
        currentQuery.value = query
    }
}
