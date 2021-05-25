package com.android.moviesapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.moviesapp.model.ItemMovie
import com.android.moviesapp.model.TypeResponse
import com.android.moviesapp.model.TypeResponse.*
import com.android.moviesapp.service.MovieApiService
import com.android.moviesapp.util.Constants.API_KEY
import retrofit2.HttpException
import java.io.IOException

class MoviesPagingSource(
    private val movieApiService: MovieApiService,
    private val type: TypeResponse,
    private val ids: List<Long>
): PagingSource<Long, ItemMovie>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, ItemMovie> {
        val position = params.key ?: 1

        return try {
            val response = when(type) {
                POPULAR -> movieApiService.getPopular(API_KEY, position)
                UPCOMING -> movieApiService.getUpcoming(API_KEY, position)
                TOPRATED -> movieApiService.getTopRated(API_KEY, position)
            }
            val movies = response.movies

            val items = arrayListOf<ItemMovie>()
            movies.forEach { items.add(ItemMovie(it, ids.contains(it.id))) }

            LoadResult.Page(
                data = items,
                prevKey = if (position == 1.toLong()) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, ItemMovie>): Long? {
        return null
    }
}