package com.android.moviesapp.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.moviesapp.db.MovieDatabase
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

open class MainViewModel(application: Application) : ViewModel() {

    val repository: MovieRepository =
        MovieRepository(MovieDatabase.getInstance(application).movieDao())

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
}
