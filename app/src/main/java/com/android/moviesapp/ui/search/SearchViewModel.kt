package com.android.moviesapp.ui.search

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.android.moviesapp.ui.main.MainViewModel
import kotlin.collections.HashMap

class SearchViewModel(application: Application) : MainViewModel(application) {

    companion object {
        private const val DEFAULT_QUERY = ""
    }

    private val discoverMap = MutableLiveData(HashMap<String, String>())
    val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val searchMovies = currentQuery.switchMap {
        repository.searchMovies(it).cachedIn(viewModelScope)
    }

    fun searchMovies(query: String) {
        currentQuery.value = query
    }

    val discoverMovies = discoverMap.switchMap {
        repository.discoverMovies(it).cachedIn(viewModelScope)
    }

    fun discoverMovies(map: HashMap<String, String>) {
        discoverMap.value = map
    }
}
