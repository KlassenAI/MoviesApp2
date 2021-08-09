package com.android.moviesapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.service.MovieApiService
import com.android.moviesapp.util.Constant.Companion.API_KEY
import com.android.moviesapp.util.Constant.Companion.KEY_API_KEY
import com.android.moviesapp.util.Constant.Companion.KEY_LANG
import com.android.moviesapp.util.Constant.Companion.KEY_PAGE
import com.android.moviesapp.util.Constant.Companion.KEY_QUERY
import com.android.moviesapp.util.Constant.Companion.KEY_REGION
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class MoviePagingSource(
    private val movieApiService: MovieApiService,
    private val map: HashMap<String, String> = hashMapOf(),
    private val type: TypeRequest? = null
): PagingSource<Long, Movie>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Movie> {

        val position = params.key ?: 1

        return try {
            map[KEY_API_KEY] = API_KEY
            map[KEY_PAGE] = position.toString()

            val language = Locale.getDefault().language
            map[KEY_LANG] = language
            when(language) {
                "ru" -> {
                    map[KEY_REGION] = language
                }
                else -> {
                    map[KEY_REGION] = "us"
                }
            }

            val response = when {
                type != null -> {
                    when(type) {
                        POPULAR -> movieApiService.getPopularMovies(map)
                        TOP -> movieApiService.getTopMovies(map)
                        UPCOMING -> movieApiService.getUpcomingMovies(map)
                    }
                }
                map[KEY_QUERY] != null -> {
                    movieApiService.searchMovies(map)
                }
                else -> {
                    movieApiService.discoverMovies(map)
                }
            }
            val result = response.body()
            val movies = result?.movies
            movies?.map { it.addDate = "" }

            LoadResult.Page(
                data = movies!!,
                prevKey = if (position == 1.toLong()) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, Movie>): Long? {
        return null
    }
}