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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.callback.AdapterCallback
import com.android.moviesapp.adapter.MovieLoadStateAdapter
import com.android.moviesapp.adapter.PagingListItemAdapter
import com.android.moviesapp.callback.SwipeCallback
import com.android.moviesapp.databinding.FragmentHomeMoviesBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn
import com.android.moviesapp.util.Expansions.Companion.withLoadStateHeaderAndFooter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
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
    private val listAdapter by lazy { PagingListItemAdapter(viewModel, this) }

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
        initAdapter()
        initButtonRefresh()
        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initToolbar()
        initViewModel()
    }

    private fun initAdapter() {
        listAdapter.addLoadStateListener {
            binding.apply {
                moviesProgress.isVisible = it.source.refresh is LoadState.Loading
                moviesSwipe.isVisible = it.source.refresh is LoadState.NotLoading
                moviesMessageError.isVisible = it.source.refresh is LoadState.Error
            }
        }
    }

    private fun initButtonRefresh() {
        binding.moviesBtnRefresh.setOnClickListener {
            listAdapter.retry()
        }
    }

    private fun initType() {
        type = (arguments?.get(TypeRequest::class.java.simpleName) as TypeRequest?)!!
        viewModel.getMovies()
    }

    private fun initRecycler() {
        binding.moviesRecycler.apply {
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = listAdapter.withLoadStateHeaderAndFooter(
                MovieLoadStateAdapter { listAdapter.retry() }
            )
        }
    }

    private fun initSwipeRefresh() {
        binding.moviesSwipe.apply {
            setOnRefreshListener {
                listAdapter.retry()
                isRefreshing = false
            }
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeCallback(requireContext(), 0, ItemTouchHelper.END) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = listAdapter.get(position)!!
                listAdapter.notifyItemChanged(position)
                addMovieWithUndo(movie, position)
            }
        }).attachToRecyclerView(binding.moviesRecycler)
    }

    private fun addMovieWithUndo(movie: Movie, position: Int) {
        GlobalScope.launch(IO) {
            val favorite = viewModel.isExist(movie.id)
            withContext(Main) {
                if (favorite) {
                    Toast.makeText(
                        context,
                        getString(R.string.movie_already_in_favorites),
                        LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.insert(movie)
                    Snackbar.make(
                        binding.moviesSnackBar, getString(R.string.movie_added), LENGTH_LONG
                    ).setAction(getString(R.string.cancel)) {
                        viewModel.delete(movie)
                        listAdapter.notifyItemChanged(position)
                    }.show()
                }
            }
        }
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.moviesToolbar)
            supportActionBar?.title = when (type) {
                POPULAR -> getString(R.string.popular)
                TOP -> getString(R.string.top)
                UPCOMING -> getString(R.string.upcoming)
            }
            supportActionBar?.setHomeBtn(true)
        }
    }

    private fun initViewModel() {
        when(type) {
            POPULAR -> viewModel.popularMovies
            TOP -> viewModel.topMovies
            UPCOMING -> viewModel.upcomingMovies
        }.observe(viewLifecycleOwner, {
            listAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun showMovieInfo(movie: Movie) {
        val bundle = bundleOf(Movie::class.java.simpleName to movie)
        navController.navigate(R.id.action_movies_to_movie, bundle)
    }
}