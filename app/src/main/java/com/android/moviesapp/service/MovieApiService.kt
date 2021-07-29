package com.android.moviesapp.service

import com.android.moviesapp.model.MovieResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MovieApiService {

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("search/movie")
    fun getSearchMovies(@Query("api_key") apiKey: String,
                        @Query("query") query: String): Call<MovieResponse>

    @GET("search/movie")
    suspend fun getSearchMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("movie/popular")
    suspend fun getPopularMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

    @GET("discover/movie")
    suspend fun getGenreMovies(@QueryMap map: Map<String, String>): Response<MovieResponse>

}