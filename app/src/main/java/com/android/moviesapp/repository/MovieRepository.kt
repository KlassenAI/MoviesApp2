package com.android.moviesapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.android.moviesapp.db.MovieDao
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.Sort
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.paging.MoviePagingSource
import com.android.moviesapp.service.MovieApiService
import com.android.moviesapp.service.RetrofitInstance.service
import com.android.moviesapp.util.Constant.Companion.KEY_GENRE
import com.android.moviesapp.util.Constant.Companion.KEY_QUERY
import com.android.moviesapp.util.Constant.Companion.TABLE_NAME
import java.text.SimpleDateFormat
import java.util.*

class MovieRepository(private val dao: MovieDao) {

    private val apiService: MovieApiService = service

    fun getFavorites(sort: Sort): LiveData<List<Movie>> {
        val query = "SELECT * FROM $TABLE_NAME ORDER BY ${sort.getSortWithName()}"
        return dao.getFavorites(SimpleSQLiteQuery(query))
    }

    suspend fun insert(movie: Movie) {
        if (movie.addDate.isEmpty()) {
            val sdf = SimpleDateFormat("y:MM:dd hh:mm:ss", Locale.getDefault())
            val date = sdf.format(Date())
            movie.addDate = date
        }
        dao.insert(movie)
    }

    suspend fun delete(movie: Movie) {
        dao.delete(movie)
    }

    suspend fun isExist(id: Long): Boolean {
        return dao.isExist(id)
    }

    fun searchMovies(query: String) =
        getPager(map = hashMapOf(KEY_QUERY to query))

    fun getByGenre(genre: String) =
        getPager(map = hashMapOf(KEY_GENRE to genre))

    fun getPopular() =
        getPager(type = POPULAR)

    fun getTop() =
        getPager(type = TOP)

    fun getUpcoming() =
        getPager(type = UPCOMING)

    fun getDiscover(map: HashMap<String, String>) = getPager(map = map)

    private fun getPager(map: HashMap<String, String> = hashMapOf(), type: TypeRequest? = null) =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
            pagingSourceFactory = { MoviePagingSource(apiService, map, type) }
        ).liveData
}