package com.android.moviesapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.android.moviesapp.model.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)

    @Query("SELECT EXISTS(SELECT * FROM movie_table WHERE id = :id)")
    suspend fun isExist(id: Long): Boolean

    @RawQuery(observedEntities = [Movie::class])
    fun getFavoriteMovies(sortQuery: SupportSQLiteQuery): LiveData<List<Movie>>
}