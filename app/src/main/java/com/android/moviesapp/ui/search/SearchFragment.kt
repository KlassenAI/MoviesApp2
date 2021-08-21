package com.android.moviesapp.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
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
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.ConcatAdapter
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
import com.android.moviesapp.databinding.FragmentSearchBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn
import com.android.moviesapp.util.Expansions.Companion.withLoadStateHeaderAndFooter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.FieldPosition

class SearchFragment : Fragment(R.layout.fragment_search), AdapterCallback {

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(activity?.application!!))
            .get(SearchViewModel::class.java)
    }

    private val listAdapter by lazy { PagingListItemAdapter(viewModel, this) }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        navController = Navigation.findNavController(view)
        initFields()
    }

    private fun initFields() {
        initAdapter()
        initEditText()
        initFilter()
        initViewModel()
        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initToolbar()
        initButtonRefresh()
    }

    private fun initAdapter() {
        listAdapter.addLoadStateListener {
            binding.apply {
                if (viewModel.currentQuery.value != "") {
                    searchMessageFirst.isVisible = false
                    searchProgress.isVisible = it.source.refresh is LoadState.Loading
                    searchSwipe.isVisible = it.source.refresh is LoadState.NotLoading
                    searchMessageError.isVisible = it.source.refresh is LoadState.Error

                    if (it.source.refresh is LoadState.NotLoading
                        && it.append.endOfPaginationReached
                        && listAdapter.itemCount < 1
                    ) {
                        searchSwipe.isVisible = false
                        searchMessageEmpty.isVisible = true
                    } else {
                        searchMessageEmpty.isVisible = false
                    }
                }
            }
        }
    }

    private fun initEditText() {
        binding.searchEditText.apply {
            setText(
                viewModel.currentQuery.value,
                TextView.BufferType.EDITABLE
            )
            setOnEditorActionListener { v, id, _ ->
                if (id == EditorInfo.IME_ACTION_DONE) {
                    val query = v.text.toString()
                    if (query.isNotEmpty()) {
                        viewModel.searchMovies(query)
                    }
                }
                false
            }
        }
    }

    private fun initFilter() {
        binding.searchFilter.setOnClickListener {
            navController.navigate(R.id.action_search_to_search_get_filter)
        }
    }

    private fun initViewModel() {
        viewModel.searchMovies.observe(viewLifecycleOwner, {
            listAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun initRecycler() {
        binding.searchRecycler.apply {
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = listAdapter.withLoadStateHeaderAndFooter(
                MovieLoadStateAdapter { listAdapter.retry() }
            )
        }
    }

    private fun initSwipeRefresh() {
        binding.searchSwipe.apply {
            setOnRefreshListener {
                viewModel.apply {
                    if (currentQuery.value != "") {
                        listAdapter.retry()
                    }
                }
                isRefreshing = false
            }
        }
    }

    private fun initSwipeToSide() {
        ItemTouchHelper(object : SwipeCallback(requireContext(), 0, END) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val movie = listAdapter.get(position)!!
                listAdapter.notifyItemChanged(position)
                addMovieWithUndo(movie, position)
            }
        }).attachToRecyclerView(binding.searchRecycler)
    }

    private fun addMovieWithUndo(movie: Movie, position: Int) {
        GlobalScope.launch(IO) {
            val favorite = viewModel.isExist(movie.id)
            withContext(Main) {
                if (favorite) {
                    Toast.makeText(context, getString(R.string.movie_already_in_favorites), LENGTH_SHORT).show()
                } else {
                    viewModel.insert(movie)
                    Snackbar.make(
                        binding.searchSnackBar, getString(R.string.movie_added), LENGTH_LONG
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
            setSupportActionBar(search_toolbar)
            supportActionBar?.setHomeBtn(false)
        }
    }

    private fun initButtonRefresh() {
        binding.searchBtnRefresh.setOnClickListener { listAdapter.retry() }
    }

    override fun showMovieInfo(movie: Movie) {
        val bundle = bundleOf(Movie::class.java.simpleName to movie)
        navController.navigate(R.id.action_search_to_movie, bundle)
    }
}
