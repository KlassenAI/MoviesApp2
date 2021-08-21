package com.android.moviesapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.android.moviesapp.db.MovieDao
import com.android.moviesapp.enums.LangEnum
import com.android.moviesapp.model.*
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.paging.MoviePagingSource
import com.android.moviesapp.service.MovieApiService
import com.android.moviesapp.service.RetrofitInstance.service
import com.android.moviesapp.util.Constant.Companion.API_KEY
import com.android.moviesapp.util.Constant.Companion.KEY_API_KEY
import com.android.moviesapp.util.Constant.Companion.KEY_GENRE
import com.android.moviesapp.util.Constant.Companion.KEY_LANG
import com.android.moviesapp.util.Constant.Companion.KEY_QUERY
import com.android.moviesapp.util.Constant.Companion.KEY_REGION
import com.android.moviesapp.util.Constant.Companion.TABLE_NAME
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MovieRepository(private val dao: MovieDao) {

    private val apiService: MovieApiService = service

    private suspend fun getSearchMovies(queryMap: HashMap<String, String>) =
        apiService.searchMovies(queryMap)

    private suspend fun getDiscoverMovies(queryMap: HashMap<String, String>) =
        apiService.discoverMovies(queryMap)

    private suspend fun getPopularMovies(queryMap: HashMap<String, String>) =
        apiService.getPopularMovies(queryMap)

    private suspend fun getTopMovies(queryMap: HashMap<String, String>) =
        apiService.getTopMovies(queryMap)

    private suspend fun getUpcomingMovies(queryMap: HashMap<String, String>) =
        apiService.getUpcomingMovies(queryMap)

    private fun getBaseQueryMap(): HashMap<String, String> = hashMapOf(
        KEY_API_KEY to API_KEY,
        KEY_LANG to getLanguage(),
        KEY_REGION to getRegion()
    )

    private fun getLanguage() = LangEnum.get(Locale.ROOT.language).getLanguage()

    private fun getRegion() = LangEnum.get(Locale.ROOT.language).getRegion()

    private fun getMoviePagingData(
        queryMap: HashMap<String, String>,
        queryMovies: suspend (HashMap<String, String>) -> Response<MovieResponse>,
    ) = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(queryMap, queryMovies) }
    ).liveData

    fun searchMovies(query: String): LiveData<PagingData<Movie>> {
        val queryMap = getBaseQueryMap()
        queryMap[KEY_QUERY] = query
        return getMoviePagingData(queryMap, ::getSearchMovies)
    }

    fun discoverMovies(queryMap: HashMap<String, String>): LiveData<PagingData<Movie>> {
        queryMap.putAll(getBaseQueryMap())
        return getMoviePagingData(queryMap, ::getDiscoverMovies)
    }

    fun requestMoviesByType(type: TypeRequest): LiveData<PagingData<Movie>> = getMoviePagingData(
        getBaseQueryMap(), when (type) {
            POPULAR -> ::getPopularMovies
            TOP -> ::getTopMovies
            UPCOMING -> ::getUpcomingMovies
        }
    )

    fun requestMoviesByGenre(genre: String): LiveData<PagingData<Movie>> {
        val queryMap = getBaseQueryMap()
        queryMap[KEY_GENRE] = genre
        return getMoviePagingData(queryMap, ::getDiscoverMovies)
    }

    suspend fun insert(movie: Movie) {
        movie.setAddDate()
        dao.insert(movie)
    }

    private fun Movie.setAddDate() {
        if (addDate.isNullOrEmpty()) {
            addDate = getCurrentDate()
        }
    }

    private fun getCurrentDate() =
        SimpleDateFormat("y:MM:dd hh:mm:ss", Locale.ROOT).format(Date())

    suspend fun delete(movie: Movie) {
        dao.delete(movie)
    }

    suspend fun isExist(id: Long): Boolean {
        return dao.isExist(id)
    }

    fun getFavoriteMovies(sortItem: SortItem): LiveData<List<Movie>> {
        val query =
            "SELECT * FROM $TABLE_NAME ORDER BY ${sortItem.getColumnName()} ${sortItem.getColumnAscending()}"
        return dao.getFavoriteMovies(SimpleSQLiteQuery(query))
    }
}