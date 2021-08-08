package com.android.moviesapp.ui.movie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.moviesapp.R
import com.android.moviesapp.databinding.FragmentMovieBinding
import com.android.moviesapp.databinding.FragmentMovieOverviewBinding
import com.android.moviesapp.model.Movie
import kotlinx.android.synthetic.main.fragment_movie.*

class MovieOverviewFragment : Fragment(R.layout.fragment_movie_overview) {

    private lateinit var binding: FragmentMovieOverviewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieOverviewBinding.bind(view)

        val movie: Movie = arguments?.getParcelable(Movie::class.java.simpleName)!!
        binding.movieOverviewText.text = movie.overview

        initToolbar(movie.title)
    }

    private fun initToolbar(title: String) {
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.movieOverviewToolbar)
            supportActionBar?.title = title
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }
}