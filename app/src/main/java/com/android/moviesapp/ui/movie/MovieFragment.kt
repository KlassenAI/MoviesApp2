package com.android.moviesapp.ui.movie

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.android.moviesapp.R
import com.android.moviesapp.adapter.GenreAdapter
import com.android.moviesapp.databinding.FragmentMovieBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.ui.main.MainViewModel
import com.android.moviesapp.util.Constant
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieFragment : Fragment(R.layout.fragment_movie) {

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(activity?.application!!))
            .get(MainViewModel::class.java)
    }
    private lateinit var navController: NavController
    private lateinit var binding: FragmentMovieBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding = FragmentMovieBinding.bind(view)

        val movie: Movie = arguments?.getParcelable(Movie::class.java.simpleName)!!
        binding.movie = movie

        initToolbar(movie.title)
        initAddBtn(movie)
        initGenreList(movie)
        initOverviewBtn(movie)
    }

    private fun initOverviewBtn(movie: Movie) {
        with(binding.movieOverview) {
            isClickable = movie.overview.isNotEmpty()
            setOnClickListener {
                if (movie.overview.isNotEmpty()) {
                    val bundle = bundleOf(Movie::class.java.simpleName to movie)
                    navController.navigate(R.id.action_movie_to_overview, bundle)
                }
            }
        }
    }

    private fun initGenreList(movie: Movie) {
        if (movie.genreIds.isNotEmpty()) {
            val genreStrings = arrayListOf<String>()
            movie.genreIds.forEach {
                genreStrings.add(Constant.getGenreById(requireContext(), it))
            }
            with(binding.movieGenresList) {
                layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
                adapter = GenreAdapter(genreStrings)
            }
        }
    }

    private fun initAddBtn(movie: Movie) {
        setAddBtnText(movie.id)

        binding.movieAddBtn.setOnClickListener {
            addOrDeleteMovie(movie)
        }
    }

    private fun setAddBtnText(movieId: Long) {
        CoroutineScope(IO).launch {
            val favorite = viewModel.isExist(movieId)
            if (favorite) binding.movieAddBtn.text = getString(R.string.movie_in_favorites)
            else binding.movieAddBtn.text = getString(R.string.add_movie_in_favorites)
        }
    }

    private fun addOrDeleteMovie(movie: Movie) {
        CoroutineScope(IO).launch {
            val favorite = viewModel.isExist(movie.id)
            withContext(Main) {
                if (favorite) {
                    viewModel.delete(movie)
                    showToast(getString(R.string.movie_deleted))
                    binding.movieAddBtn.text = getString(R.string.add_movie_in_favorites)
                } else {
                    viewModel.insert(movie)
                    showToast(getString(R.string.movie_added))
                    binding.movieAddBtn.text = getString(R.string.movie_in_favorites)
                }
            }
        }
    }

    private fun initToolbar(title: String) {
        binding.movieToolbar.title = title
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.movieToolbar)
            supportActionBar?.setHomeBtn(true)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
