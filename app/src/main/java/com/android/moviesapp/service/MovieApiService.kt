package com.android.moviesapp.service

import com.android.moviesapp.model.MovieResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

open interface MovieApiService {

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("search/movie")
    fun getSearchMovies(@Query("api_key") apiKey: String,
                        @Query("query") query: String) : Call<MovieResponse>


    @GET("search/movie")
    suspend fun getSearch(
            @Query("api_key") apiKey: String,
            @Query("query") query: String,
            @Query("page") page: Long
    ): MovieResponse

    @GET("search/movie")
    suspend fun getSearch(@QueryMap map: HashMap<String, String>): MovieResponse

    @GET("movie/popular")
    suspend fun getPopular(
            @Query("api_key") apiKey: String,
            @Query("page") page: Long
    ): MovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcoming(
            @Query("api_key") apiKey: String,
            @Query("page") page: Long
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRated(
            @Query("api_key") apiKey: String,
            @Query("page") page: Long
    ): MovieResponse
}