package com.android.moviesapp.ui.watchlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.Sort
import com.android.moviesapp.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG

class WatchListWatchList : Fragment(R.layout.fragment_watch_list), AdapterCallback,
    WatchListDialogCallback {

    private val viewModel by lazy { (activity as MainActivity).viewModel }
    private val adapter by lazy { MovieListItemAdapter(listOf(), viewModel, this) }

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
        initToolbar()
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
                viewModel.getFavorites(viewModel.currentSort.value!!)
                isRefreshing = false
            }
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeCallback(requireContext(), 0, START) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = adapter.get(position)
                adapter.notifyItemChanged(position)
                viewModel.delete(movie)

                Snackbar.make(
                    binding.watchListSnackBar, getString(R.string.movie_deleted), LENGTH_LONG
                ).setAction(getString(R.string.cancel)) {
                    viewModel.insert(movie)
                    adapter.notifyItemChanged(position)
                }.show()
            }
        }).attachToRecyclerView(binding.watchListRecycler)
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.watchListToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }
        setHasOptionsMenu(true)
    }

    private fun initViewModel() {
        viewModel.favorites.observe(viewLifecycleOwner, {
            binding.watchListTextEmpty.isVisible = it.isEmpty()
            binding.watchListSwipe.isVisible = it.isNotEmpty()
            adapter.setList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.watch_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> {
                val dialog = WatchListDialog(viewModel.currentSort.value!!, this)
                dialog.show(childFragmentManager, "dialog")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showMovieInfo(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable(Movie::class.java.simpleName, movie)
        navController.navigate(
            R.id.action_watch_to_movie,
            bundle
        )
    }

    override fun setSort(sort: Sort) {
        viewModel.getFavorites(sort)
    }
}