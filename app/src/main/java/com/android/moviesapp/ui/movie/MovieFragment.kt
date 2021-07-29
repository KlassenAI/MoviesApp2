package com.android.moviesapp.ui.movie

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.android.moviesapp.R
import com.android.moviesapp.databinding.FragmentMovieBinding
import com.android.moviesapp.model.Movie

class MovieFragment : Fragment(R.layout.fragment_movie) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movie: Movie = arguments?.getParcelable(Movie::class.java.simpleName)!!
        val binding = DataBindingUtil.setContentView<FragmentMovieBinding>(
            requireActivity(), R.layout.fragment_movie
        )
        binding.movie = movie
    }
}