package com.android.moviesapp.ui.watchlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.MovieAdapterCallback
import com.android.moviesapp.adapter.MovieListItemAdapter
import com.android.moviesapp.databinding.FragmentWatchListBinding
import com.android.moviesapp.model.Movie
import com.android.moviesapp.swipe.SwipeToSideHelper
import com.android.moviesapp.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG

class WatchListFragment : Fragment(R.layout.fragment_watch_list), MovieAdapterCallback {

    private val viewModel by lazy { (activity as MainActivity).viewModel }
    private val adapter by lazy { MovieListItemAdapter(ArrayList(), viewModel, true, this) }

    private lateinit var binding: FragmentWatchListBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWatchListBinding.bind(view)
        navController = Navigation.findNavController(view)
        initFields()
    }

    private fun initFields() {
        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initViewModel()
    }

    private fun initRecycler() {
        binding.watchListRecycler.also {
            it.layoutManager = LinearLayoutManager(context, VERTICAL, false)
            it.adapter = adapter
        }
    }

    private fun initSwipeRefresh() {
        binding.watchListSwipe.run {
            setOnRefreshListener {
                viewModel.favoriteMovies
            }
            isRefreshing = false
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeToSideHelper(requireContext(), 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = adapter.get(position)
                adapter.notifyItemChanged(position)

                viewModel.delete(movie)
                Snackbar.make(
                    binding.watchListSnackBar, "Фильм удален из избранных", LENGTH_LONG
                ).setAction("Отмена") {
                    viewModel.insert(movie)
                }.show()
            }
        }).apply { attachToRecyclerView(binding.watchListRecycler) }
    }

    private fun initViewModel() {
        viewModel.favoriteMovies.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                binding.watchListMessageEmpty.isVisible = false
                binding.watchListSwipe.isVisible = true
            } else {
                binding.watchListMessageEmpty.isVisible = true
                binding.watchListSwipe.isVisible = false
            }
            adapter.setList(it)
        })
    }

    override fun showFullInfo(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable(Movie::class.java.simpleName, movie)
        navController.navigate(R.id.action_watch_to_movie, bundle)
    }
}