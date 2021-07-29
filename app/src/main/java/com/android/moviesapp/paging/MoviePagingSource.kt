package com.android.moviesapp.paging

import android.util.Log
import androidx.collection.arrayMapOf
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.service.MovieApiService
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class MoviePagingSource(
    private val movieApiService: MovieApiService,
    private val query: String? = null,
    private val genre: String? = null,
    private val type: TypeRequest = TOP
): PagingSource<Long, Movie>() {

    companion object {
        private const val KEY_API_KEY = "api_key"
        private const val API_KEY = "bbc469558822b67257a0bd5a0534463e"
        private const val KEY_LANG = "language"
        private const val LANG_RU = "ru"
        private const val KEY_QUERY = "query"
        private const val KEY_PAGE = "page"
        private const val KEY_GENRE = "with_genres"
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Movie> {
        val position = params.key ?: 1

        return try {
            val map = arrayMapOf<String, String>()
            map[KEY_API_KEY] = API_KEY
            map[KEY_LANG] = LANG_RU
            if (query != null) map[KEY_QUERY] = query
            if (genre != null) map[KEY_GENRE] = genre
            map[KEY_PAGE] = position.toString()

            val response = when {
                query != null -> {
                    movieApiService.getSearchMovies(map)
                }
                genre != null -> {
                    movieApiService.getGenreMovies(map)
                }
                else -> {
                    when(type) {
                        POPULAR -> movieApiService.getPopularMovies(map)
                        TOP -> movieApiService.getTopMovies(map)
                        UPCOMING -> movieApiService.getUpcomingMovies(map)
                    }
                }
            }
            val result = response.body()
            val movies = result?.movies!!

            LoadResult.Page(
                data = movies,
                prevKey = if (position == 1.toLong()) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            Log.e("tag", e.message.toString())
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.e("tag", e.message.toString())
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e("tag", e.message.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, Movie>): Long? {
        return null
    }
}