package com.android.moviesapp.model

import com.google.gson.annotations.SerializedName

data class MovieResponse (
	val page : Int,
	@SerializedName("results")
	val movies : ArrayList<Movie>,
	@SerializedName("total_pages")
	val total_pages : Int,
	@SerializedName("total_results")
	val total_results : Int
)