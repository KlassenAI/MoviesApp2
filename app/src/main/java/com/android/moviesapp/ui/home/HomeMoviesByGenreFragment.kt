package com.android.moviesapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.AdapterCallback
import com.android.moviesapp.adapter.MovieLoadStateAdapter
import com.android.moviesapp.adapter.PagingListItemAdapter
import com.android.moviesapp.databinding.FragmentHomeMoviesByGenreBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.swipe.SwipeToSideHelper
import com.android.moviesapp.util.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeMoviesByGenreFragment : Fragment(R.layout.fragment_home_movies_by_genre), AdapterCallback {

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(activity?.application!!))
            .get(HomeViewModel::class.java)
    }
    private val adapter by lazy { PagingListItemAdapter(viewModel, true, this) }

    private lateinit var binding: FragmentHomeMoviesByGenreBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeMoviesByGenreBinding.bind(view)
        navController = Navigation.findNavController(view)
        initFields()
    }

    private fun initFields() {
        initAdapter()
        initButtonRefresh()
        initList()
        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initViewModel()
    }

    private fun initAdapter() {
        adapter.addLoadStateListener {
            binding.apply {
                if (!moviesByGenreList.isVisible) {
                    moviesByGenreProgress.isVisible = it.source.refresh is LoadState.Loading
                    moviesByGenreSwipe.isVisible = it.source.refresh is LoadState.NotLoading
                    moviesByGenreMessageError.isVisible = it.source.refresh is LoadState.Error
                }
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

    private fun initList() {
        binding.run {
            moviesByGenreList.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, Constants.genreArray)
            moviesByGenreList.setOnItemClickListener { _, _, position, _ ->
                viewModel.getGenreMovies(Constants.getKey(position).toString())
                moviesByGenreList.isVisible = false
                moviesByGenreRecycler.isVisible = true
            }
        }
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
                viewModel.also {
                    adapter.retry()
                }
            }
            isRefreshing = false
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeToSideHelper(requireContext(), 0, ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = adapter.get(position)
                adapter.notifyItemChanged(position)

                GlobalScope.launch(Dispatchers.IO) {
                    val favorite = viewModel.isExist(movie?.id!!)
                    withContext(Dispatchers.Main) {
                        if (favorite) {
                            Toast.makeText(
                                context, "Фильм уже в избранных", LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.insert(movie)
                            Snackbar.make(
                                binding.moviesByGenreSnackBar, "Фильм добавлен в избранные", LENGTH_LONG
                            ).setAction("Отмена") {
                                viewModel.delete(movie)
                                adapter.notifyItemChanged(position)
                            }.show()
                        }
                    }
                }
            }
        }).apply { attachToRecyclerView(binding.moviesByGenreRecycler) }
    }

    private fun initViewModel() {
        viewModel.genreMovies.observe(viewLifecycleOwner, {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun showFullInfo(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable(Movie::class.java.simpleName, movie)
        navController.navigate(R.id.action_movies_by_genre_to_movie, bundle)
    }
}
