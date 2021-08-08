package com.android.moviesapp.ui.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.android.moviesapp.db.MovieDatabase
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.Sort
import com.android.moviesapp.model.Sort.ADD_DATE_DESC
import com.android.moviesapp.repository.MovieRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

open class MainViewModel(application: Application) : ViewModel() {

    companion object {
        private const val PREF_SORT = "sort"
    }

    private val sharedPreferences: SharedPreferences = application.applicationContext
        .getSharedPreferences(PREF_SORT, Context.MODE_PRIVATE)

    val repository: MovieRepository =
        MovieRepository(MovieDatabase.getInstance(application).movieDao())

    val currentSort = MutableLiveData(getSort())

    val favorites = currentSort.switchMap {
        repository.getFavorites(it)
    }

    fun getFavorites(sort: Sort) {
        currentSort.postValue(sort)
        saveSort(sort)
    }

    fun insert(movie: Movie) {
        viewModelScope.launch(IO) {
            repository.insert(movie)
        }
    }

    fun delete(movie: Movie) {
        viewModelScope.launch(IO) {
            repository.delete(movie)
        }
    }

    suspend fun isExist(id: Long): Boolean {
        return repository.isExist(id)
    }

    private fun saveSort(sort: Sort) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(sort)
        editor.putString(Sort::class.java.simpleName, json)
        editor.apply()
    }

    private fun getSort(): Sort {
        val json = sharedPreferences.getString(Sort::class.java.simpleName, "")
        if (json == "") return ADD_DATE_DESC
        return Gson().fromJson(json, Sort::class.java)
    }
}
