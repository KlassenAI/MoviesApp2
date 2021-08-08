package com.android.moviesapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.callback.AdapterCallback
import com.android.moviesapp.adapter.MovieLoadStateAdapter
import com.android.moviesapp.adapter.PagingListItemAdapter
import com.android.moviesapp.callback.SwipeCallback
import com.android.moviesapp.databinding.FragmentHomeMoviesByGenreBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.util.Constant
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeMoviesByGenreFragment : Fragment(R.layout.fragment_home_movies_by_genre),
    AdapterCallback {

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(activity?.application!!))
            .get(HomeViewModel::class.java)
    }
    private val adapter by lazy { PagingListItemAdapter(viewModel, this) }

    private lateinit var binding: FragmentHomeMoviesByGenreBinding
    private lateinit var navController: NavController
    private lateinit var genre: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeMoviesByGenreBinding.bind(view)
        navController = Navigation.findNavController(view)
        initFields()
    }

    private fun initFields() {
        initAdapter()
        initButtonRefresh()
        initGenre()
        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initToolbar()
        initViewModel()
    }

    private fun initAdapter() {
        adapter.addLoadStateListener {
            binding.apply {
                moviesByGenreProgress.isVisible = it.source.refresh is LoadState.Loading
                moviesByGenreSwipe.isVisible = it.source.refresh is LoadState.NotLoading
                moviesByGenreMessageError.isVisible = it.source.refresh is LoadState.Error
            }
        }
    }

    private fun initButtonRefresh() {
        binding.apply {
            moviesByGenreBtnRefresh.setOnClickListener {
                adapter.retry()
            }
        }
    }

    private fun initGenre() {
        genre = arguments?.getString(Constant.BUNDLE_KEY_GENRE)!!
        viewModel.getGenreMovies(genre)
    }

    private fun initRecycler() {
        binding.moviesByGenreRecycler.also {
            it.layoutManager = LinearLayoutManager(context, VERTICAL, false)
            it.adapter = adapter.withLoadStateHeaderAndFooter(
                MovieLoadStateAdapter { adapter.retry() },
                MovieLoadStateAdapter { adapter.retry() }
            )
        }
    }

    private fun initSwipeRefresh() {
        binding.moviesByGenreSwipe.run {
            setOnRefreshListener {
                adapter.retry()
                isRefreshing = false
            }
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeCallback(requireContext(), 0, END) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = adapter.get(position)
                adapter.notifyItemChanged(position)

                GlobalScope.launch(IO) {
                    val favorite = viewModel.isExist(movie?.id!!)
                    withContext(Main) {
                        if (favorite) {
                            Toast.makeText(
                                context, getString(R.string.movie_already_in_favorites), LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.insert(movie)
                            Snackbar.make(
                                binding.moviesByGenreSnackBar, getString(R.string.movie_added), LENGTH_LONG
                            ).setAction(getString(R.string.cancel)) {
                                viewModel.delete(movie)
                                adapter.notifyItemChanged(position)
                            }.show()
                        }
                    }
                }
            }
        }).attachToRecyclerView(binding.moviesByGenreRecycler)
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.moviesByGenreToolbar)
            supportActionBar?.title = getString(R.string.title_movies_by_genre) + " " +
                    Constant.getGenreById(requireContext(), genre.toInt())
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    private fun initViewModel() {
        viewModel.genreMovies.observe(viewLifecycleOwner, {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun showMovieInfo(movie: Movie) {
        val bundle = bundleOf(
            Movie::class.java.simpleName to movie
        )
        navController.navigate(
            R.id.action_movies_by_genre_to_movie,
            bundle
        )
    }
}
