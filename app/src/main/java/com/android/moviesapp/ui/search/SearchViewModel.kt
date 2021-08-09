package com.android.moviesapp.ui.search

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.android.moviesapp.ui.main.MainViewModel
import java.util.*
import kotlin.collections.HashMap

class SearchViewModel(application: Application) : MainViewModel(application) {

    companion object {
        private const val DEFAULT_QUERY = ""
    }

    val currentQuery = MutableLiveData(DEFAULT_QUERY)
    private val discoverMap = MutableLiveData(HashMap<String, String>())

    val searchMovies = currentQuery.switchMap {
        repository.searchMovies(it).cachedIn(viewModelScope)
    }

    fun searchMovies(query: String) {
        currentQuery.value = query
    }

    val discoverMovies = discoverMap.switchMap {
        repository.getDiscover(it).cachedIn(viewModelScope)
    }

    fun discoverMovies(map: HashMap<String, String>) {
        discoverMap.value = map
    }
}
