package com.android.moviesapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.android.moviesapp.databinding.FragmentHomeMoviesBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.swipe.SwipeToSideHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeMoviesFragment : Fragment(R.layout.fragment_home_movies), AdapterCallback {

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(activity?.application!!))
            .get(HomeViewModel::class.java)
    }
    private val popularMoviesAdapter by lazy { PagingListItemAdapter(viewModel, true, this) }
    private val upcomingMoviesAdapter by lazy { PagingListItemAdapter(viewModel, true, this) }
    private val topMoviesAdapter by lazy { PagingListItemAdapter(viewModel, true, this) }

    private lateinit var binding: FragmentHomeMoviesBinding
    private lateinit var navController: NavController
    private lateinit var type: TypeRequest

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding = FragmentHomeMoviesBinding.bind(view)
        initFields()
    }

    private fun initFields() {
        initType()
        initAdapters()
        initButtonRefresh()
        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initViewModel()
    }

    private fun initAdapters() {
        when (type) {
            POPULAR -> initAdapter(popularMoviesAdapter)
            TOP -> initAdapter(topMoviesAdapter)
            UPCOMING -> initAdapter(upcomingMoviesAdapter)
        }
    }

    private fun initAdapter(adapter: PagingListItemAdapter) {
        adapter.addLoadStateListener {
            binding.apply {
                moviesProgress.isVisible = it.source.refresh is LoadState.Loading
                moviesSwipe.isVisible = it.source.refresh is LoadState.NotLoading
                moviesMessageError.isVisible = it.source.refresh is LoadState.Error
            }
        }
    }

    private fun initButtonRefresh() {
        binding.apply {
            moviesBtnRefresh.setOnClickListener {
                when (type) {
                    POPULAR -> popularMoviesAdapter.retry()
                    TOP -> topMoviesAdapter.retry()
                    UPCOMING -> upcomingMoviesAdapter.retry()
                }
            }
        }
    }

    private fun initType() {
        type = (arguments?.get(TypeRequest::class.java.simpleName) as TypeRequest?)!!
        viewModel.getMovies()
    }

    private fun initRecycler() {
        binding.moviesRecycler.run {
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = when (type) {
                POPULAR -> popularMoviesAdapter.withLoadStateHeaderAndFooter(
                    MovieLoadStateAdapter { popularMoviesAdapter.retry() },
                    MovieLoadStateAdapter { popularMoviesAdapter.retry() }
                )
                TOP -> topMoviesAdapter.withLoadStateHeaderAndFooter(
                    MovieLoadStateAdapter { topMoviesAdapter.retry() },
                    MovieLoadStateAdapter { topMoviesAdapter.retry() }
                )
                UPCOMING -> upcomingMoviesAdapter.withLoadStateHeaderAndFooter(
                    MovieLoadStateAdapter { upcomingMoviesAdapter.retry() },
                    MovieLoadStateAdapter { upcomingMoviesAdapter.retry() }
                )
            }
        }
    }

    private fun initSwipeRefresh() {
        binding.moviesSwipe.run {
            setOnRefreshListener {
                viewModel.also {
                    when (type) {
                        POPULAR -> popularMoviesAdapter.retry()
                        TOP -> topMoviesAdapter.retry()
                        UPCOMING -> upcomingMoviesAdapter.retry()
                    }
                }
            }
            isRefreshing = false
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeToSideHelper(requireContext(), 0, ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = when (type) {
                    POPULAR -> popularMoviesAdapter.get(position)
                    TOP -> topMoviesAdapter.get(position)
                    UPCOMING -> upcomingMoviesAdapter.get(position)
                }
                when (type) {
                    POPULAR -> popularMoviesAdapter.notifyItemChanged(position)
                    TOP -> topMoviesAdapter.notifyItemChanged(position)
                    UPCOMING -> upcomingMoviesAdapter.notifyItemChanged(position)
                }

                GlobalScope.launch(IO) {
                    val favorite = viewModel.isExist(movie?.id!!)
                    withContext(Main) {
                        if (favorite) {
                            Toast.makeText(
                                context, "Фильм уже в избранных", LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.insert(movie)
                            Snackbar.make(
                                binding.moviesSnackBar, "Фильм добавлен в избранные", LENGTH_LONG
                            ).setAction("Отмена") {
                                viewModel.delete(movie)
                                when (type) {
                                    POPULAR -> popularMoviesAdapter.notifyItemChanged(position)
                                    TOP -> topMoviesAdapter.notifyItemChanged(position)
                                    UPCOMING -> upcomingMoviesAdapter.notifyItemChanged(position)
                                }
                            }.show()
                        }
                    }
                }
            }
        }).apply { attachToRecyclerView(binding.moviesRecycler) }
    }

    private fun initViewModel() {
        when (type) {
            POPULAR -> {
                viewModel.popularMovies.observe(viewLifecycleOwner, {
                    popularMoviesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                })
            }
            TOP -> {
                viewModel.topMovies.observe(viewLifecycleOwner, {
                    topMoviesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                })
            }
            UPCOMING -> {
                viewModel.upcomingMovies.observe(viewLifecycleOwner, {
                    upcomingMoviesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                })
            }
        }
    }

    override fun showFullInfo(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable(Movie::class.java.simpleName, movie)
        navController.navigate(R.id.action_movies_to_movie, bundle)
    }
}