package com.android.moviesapp.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.MovieResponse
import com.android.moviesapp.util.Constant.Companion.KEY_PAGE
import retrofit2.Response

class MoviePagingSource(
    private val queryMap: HashMap<String, String>,
    private val requestMovies: suspend (HashMap<String, String>) -> Response<MovieResponse>
): PagingSource<Long, Movie>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Movie> {

        val position = params.key ?: 1

        return try {
            queryMap[KEY_PAGE] = position.toString()
            val response = requestMovies(queryMap)
            val result = response.body()
            val movies = result?.movies

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