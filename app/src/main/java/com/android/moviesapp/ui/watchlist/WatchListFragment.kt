package com.android.moviesapp.ui.watchlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.callback.AdapterCallback
import com.android.moviesapp.adapter.MovieListItemAdapter
import com.android.moviesapp.callback.WatchListDialogCallback
import com.android.moviesapp.callback.SwipeCallback
import com.android.moviesapp.databinding.FragmentWatchListBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.SortItem
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG

class WatchListFragment : Fragment(R.layout.fragment_watch_list), AdapterCallback, WatchListDialogCallback {

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(activity?.application!!))
            .get(WatchListViewModel::class.java)
    }
    private val listAdapter by lazy { MovieListItemAdapter(listOf(), viewModel, this) }

    private lateinit var binding: FragmentWatchListBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWatchListBinding.bind(view)
        navController = Navigation.findNavController(view)

        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initToolbar()
        initViewModel()
    }

    private fun initRecycler() {
        binding.watchListRecycler.apply {
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = listAdapter
        }
    }

    private fun initSwipeRefresh() {
        binding.watchListSwipe.apply {
            setOnRefreshListener {
                viewModel.getFavorites(viewModel.currentSort.value!!)
                isRefreshing = false
            }
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeCallback(requireContext(), 0, START) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = listAdapter.get(position)
                listAdapter.notifyItemChanged(position)
                deleteMovieWithUndo(movie, position)
            }
        }).attachToRecyclerView(binding.watchListRecycler)
    }

    private fun deleteMovieWithUndo(movie: Movie, position: Int) {
        viewModel.delete(movie)
        Snackbar.make(
            binding.watchListSnackBar, getString(R.string.movie_deleted), LENGTH_LONG
        ).setAction(getString(R.string.cancel)) {
            viewModel.insert(movie)
            listAdapter.notifyItemChanged(position)
        }.show()
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.watchListToolbar)
            supportActionBar?.setHomeBtn(false)
        }
        setHasOptionsMenu(true)
    }

    private fun initViewModel() {
        viewModel.favoriteMovies.observe(viewLifecycleOwner, {
            binding.watchListTextEmpty.isVisible = it.isEmpty()
            binding.watchListSwipe.isVisible = it.isNotEmpty()
            listAdapter.setList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.watch_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> {
                val dialog = SetSortDialog(viewModel.currentSort.value!!, this)
                dialog.show(childFragmentManager, "dialog")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showMovieInfo(movie: Movie) {
        val bundle = bundleOf(Movie::class.java.simpleName to movie)
        navController.navigate(R.id.action_watch_to_movie, bundle)
    }

    override fun setSortList(sortItem: SortItem) {
        viewModel.getFavorites(sortItem)
    }
}