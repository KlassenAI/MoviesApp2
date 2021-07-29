package com.android.moviesapp.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.moviesapp.model.GenreIdsConverter
import com.android.moviesapp.model.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(GenreIdsConverter::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var instance: MovieDatabase? = null

        fun getInstance(application: Application): MovieDatabase {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val inst = Room.databaseBuilder(application.applicationContext,
                        MovieDatabase::class.java, "movie_database")
                        .build()
                instance = inst
                return instance as MovieDatabase
            }
        }
    }
}