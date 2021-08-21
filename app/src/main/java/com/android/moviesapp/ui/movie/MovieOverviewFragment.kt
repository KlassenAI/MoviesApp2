package com.android.moviesapp.ui.movie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.android.moviesapp.R
import com.android.moviesapp.databinding.FragmentMovieOverviewBinding
import com.android.moviesapp.model.Movie
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn

class MovieOverviewFragment : Fragment(R.layout.fragment_movie_overview) {

    private lateinit var binding: FragmentMovieOverviewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMovieOverviewBinding.bind(view)
        val movie: Movie = arguments?.getParcelable(Movie::class.java.simpleName)!!

        initOverview(movie.overview)
        initToolbar(movie.title)
    }

    private fun initOverview(overview: String) {
        binding.movieOverviewText.text = overview
    }

    private fun initToolbar(title: String) {
        binding.movieOverviewToolbar.title = title
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.movieOverviewToolbar)
            supportActionBar?.setHomeBtn(true)
        }
    }
}
