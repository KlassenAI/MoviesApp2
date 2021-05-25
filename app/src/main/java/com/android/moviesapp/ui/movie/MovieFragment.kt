package com.android.moviesapp.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.moviesapp.R
import com.android.moviesapp.databinding.FragmentMovieBinding
import com.android.moviesapp.db.MovieDatabase
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieRepository

class MovieFragment : Fragment() {

    private var viewModel: MovieViewModel? = null
    private var button: Button? = null
    private var movie: Movie? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val repository = MovieRepository(MovieDatabase.getInstance(activity?.application!!).movieDao())
        val viewModelFactory = ViewModelFactory(repository, TypeModel.MOVIE)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieViewModel::class.java)

        movie = arguments?.getParcelable("movie")
        (activity as AppCompatActivity).supportActionBar?.title = movie?.title
        val binding: FragmentMovieBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie, container, false)
        binding.movie = movie

        button = binding.root.findViewById(R.id.add_button)
        var favorite = true
        if (favorite) button?.text = getString(R.string.in_favorites)
        button?.setOnClickListener {
            if (favorite) {
                button?.text = getString(R.string.add_to_favorites)
                viewModel?.delete(movie)
                Toast.makeText(context, "Movie was deleted", Toast.LENGTH_SHORT).show()
            } else {
                button?.text = getString(R.string.in_favorites)
                viewModel?.insert(movie)
                Toast.makeText(context, "Movie was added", Toast.LENGTH_SHORT).show()
            }
            favorite = !favorite
        }

        return binding.root
    }
}