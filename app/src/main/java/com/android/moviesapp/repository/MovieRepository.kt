package com.android.moviesapp.repository

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.android.moviesapp.db.MovieDao
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.MovieResponse
import com.android.moviesapp.model.TypeResponse
import com.android.moviesapp.paging.MoviesPagingSource
import com.android.moviesapp.paging.SearchPagingSource
import com.android.moviesapp.service.MovieApiService
import com.android.moviesapp.service.RetrofitInstance.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class MovieRepository(private val movieDao: MovieDao) {
    private val movieApiService: MovieApiService = service

    companion object {
        private const val API_KEY = "bbc469558822b67257a0bd5a0534463e"
    }

    fun getFavoriteMovies() : LiveData<List<Movie>> {
        return movieDao.getMovies()
    }

    private fun getFavoriteMovieIds() : List<Long> {
        val callable = Callable<List<Long>> {
            return@Callable movieDao.getMovieIds()
        }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        return future.get()
    }

    fun insert(movie: Movie?) {
        InsertMovieAsyncTask(movieDao).execute(movie)
    }

    private class InsertMovieAsyncTask(private val movieDao: MovieDao) : AsyncTask<Movie?, Void?, Void?>() {
        override fun doInBackground(vararg movies: Movie?): Void? {
            movieDao.insert(movies[0]!!)
            return null
        }
    }

    fun delete(movie: Movie?) {
        DeleteMovieAsyncTask(movieDao).execute(movie)
    }

    private class DeleteMovieAsyncTask(private val movieDao: MovieDao) : AsyncTask<Movie?, Void?, Void?>() {
        override fun doInBackground(vararg movies: Movie?): Void? {
            movieDao.delete(movies[0]!!)
            return null
        }
    }

    fun requestPopularMovies(callback: MovieCallback) {
        val call = movieApiService.getPopularMovies(Companion.API_KEY)
        call.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(call: Call<MovieResponse?>, response: Response<MovieResponse?>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse != null) {
                        val movies = movieResponse.movies
                        callback.onResponse(movies)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                Log.e("requestPopularMovies", "onFailure")
            }
        })
    }

    fun requestUpcomingMovies(callback: MovieCallback) {
        val call = movieApiService.getUpcomingMovies(Companion.API_KEY)
        call.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(call: Call<MovieResponse?>, response: Response<MovieResponse?>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse != null) {
                        val movies = movieResponse.movies
                        callback.onResponse(movies)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                Log.e("requestUpcomingMovies", "onFailure")
            }
        })
    }

    fun requestTopRatedMovies(callback: MovieCallback) {
        val call = movieApiService.getTopRatedMovies(API_KEY)
        call.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(call: Call<MovieResponse?>, response: Response<MovieResponse?>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse != null) {
                        val movies = movieResponse.movies
                        callback.onResponse(movies)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                Log.e("requestTopRatedMovies", "onFailure")
            }
        })
    }

    fun getSearchResults(query: String) = Pager(
            config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(movieApiService, query, getFavoriteMovieIds())}
    ).liveData

    fun getPopularMovies() = Pager(
            config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
            pagingSourceFactory = { MoviesPagingSource(movieApiService, TypeResponse.POPULAR, getFavoriteMovieIds())}
    ).liveData

    fun getUpcoming() = Pager(
            config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
            pagingSourceFactory = { MoviesPagingSource(movieApiService, TypeResponse.UPCOMING, getFavoriteMovieIds())}
    ).liveData

    fun getTopRated() = Pager(
            config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
            pagingSourceFactory = { MoviesPagingSource(movieApiService, TypeResponse.TOPRATED, getFavoriteMovieIds())}
    ).liveData
}