package com.android.moviesapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.android.moviesapp.model.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = IGNORE)
    fun insert(movie: Movie)

    @Delete
    fun delete(movie: Movie)

    @Query("SELECT * FROM movie_table")
    fun getMovies() : LiveData<List<Movie>>

    @Query("SELECT id FROM movie_table")
    fun getMovieIds() : List<Long>

    @Query("SELECT EXISTS(SELECT * FROM movie_table WHERE id = :id)")
    fun isMovieExist(id : Long) : Boolean
}