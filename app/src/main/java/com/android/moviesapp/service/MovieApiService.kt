package com.android.moviesapp.service

import com.android.moviesapp.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface MovieApiService {

    @GET("search/movie")
    suspend fun searchMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("movie/popular")
    suspend fun getPopularMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("discover/movie")
    suspend fun discoverMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>
}
