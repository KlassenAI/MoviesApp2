package com.android.moviesapp.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
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
import com.android.moviesapp.adapter.PagingListItemAdapter
import com.android.moviesapp.databinding.FragmentSearchBinding
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.swipe.SwipeToSideHelper
import com.android.moviesapp.adapter.MovieLoadStateAdapter
import com.android.moviesapp.model.Movie
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(R.layout.fragment_search), AdapterCallback {

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(activity?.application!!))
            .get(SearchViewModel::class.java)
    }

    private val adapter by lazy { PagingListItemAdapter(viewModel, true, this) }

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
        initViewModel()
        initRecycler()
        initSwipeRefresh()
        initSwipeToSide()
        initButtonRefresh()
    }

    private fun initAdapter() {
        adapter.addLoadStateListener {
            binding.apply {
                if (viewModel.currentQuery.value != "") {
                    searchMessageFirst.isVisible = false
                    searchProgress.isVisible = it.source.refresh is LoadState.Loading
                    searchSwipe.isVisible = it.source.refresh is LoadState.NotLoading
                    searchMessageError.isVisible = it.source.refresh is LoadState.Error

                    if (it.source.refresh is LoadState.NotLoading
                        && it.append.endOfPaginationReached
                        && adapter.itemCount < 1
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
        binding.searchEditText.run {
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

    private fun initViewModel() {
        viewModel.searchMovies.observe(viewLifecycleOwner, {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun initRecycler() {
        binding.searchRecycler.also {
            it.layoutManager = LinearLayoutManager(context, VERTICAL, false)
            it.adapter = adapter.withLoadStateHeaderAndFooter(
                MovieLoadStateAdapter { adapter.retry() },
                MovieLoadStateAdapter { adapter.retry() }
            )
        }
    }

    private fun initSwipeRefresh() {
        binding.searchSwipe.run {
            setOnRefreshListener {
                viewModel.also {
                    if (it.currentQuery.value != "") {
                        adapter.retry()
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
                val movie = adapter.get(position)
                adapter.notifyItemChanged(position)

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
                                binding.searchSnack, "Фильм добавлен в избранные", LENGTH_LONG
                            ).setAction("Отмена") {
                                viewModel.delete(movie)
                                adapter.notifyItemChanged(position)
                            }.show()
                        }
                    }
                }
            }
        }).apply { attachToRecyclerView(binding.searchRecycler) }
    }

    private fun initButtonRefresh() {
        binding.apply {
            searchBtnRefresh.setOnClickListener {
                adapter.retry()
            }
        }
    }

    override fun showFullInfo(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable(Movie::class.java.simpleName, movie)
        navController.navigate(R.id.action_search_to_movie, bundle)
    }
}
