package com.android.moviesapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.model.TypeModel.*
import com.android.moviesapp.repository.MovieRepository
import com.android.moviesapp.ui.home.HomeMoviesViewModel
import com.android.moviesapp.ui.search.SearchViewModel
import com.android.moviesapp.ui.home.HomeViewModel
import com.android.moviesapp.ui.movie.MovieViewModel
import com.android.moviesapp.ui.wishlist.WishlistViewModel

class ViewModelFactory(
        private val repository: MovieRepository,
        private val typeModel: TypeModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(typeModel) {
            SEARCH -> SearchViewModel(repository) as T
            HOME -> HomeViewModel(repository) as T
            WISHLIST -> WishlistViewModel(repository) as T
            HOME_MOVIES -> HomeMoviesViewModel(repository) as T
            MOVIE -> MovieViewModel(repository) as T
        }
    }
}
