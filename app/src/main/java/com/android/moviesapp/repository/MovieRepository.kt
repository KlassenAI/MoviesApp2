package com.android.moviesapp.repository

import androidx.collection.arrayMapOf
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.android.moviesapp.db.MovieDao
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.paging.MoviePagingSource
import com.android.moviesapp.service.MovieApiService
import com.android.moviesapp.service.RetrofitInstance.service

class MovieRepository(private val dao: MovieDao) {
    private val apiService: MovieApiService = service

    fun getMovies(): LiveData<List<Movie>> {
        return dao.getMovies()
    }

    suspend fun insert(movie: Movie) {
        dao.insert(movie)
    }

    suspend fun delete(movie: Movie) {
        dao.delete(movie)
    }

    suspend fun isExist(id: Long): Boolean {
        return dao.isExist(id)
    }

    fun getSearchMovies(query: String) = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(apiService, query = query) }
    ).liveData

    fun getPopular() = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(apiService, type = POPULAR) }
    ).liveData

    fun getTop() = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(apiService, type = TOP) }
    ).liveData

    fun getUpcoming() = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(apiService, type = UPCOMING) }
    ).liveData

    fun getByGenre(genre: String) = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(apiService, genre = genre) }
    ).liveData
}