package com.android.moviesapp.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.moviesapp.R
import com.android.moviesapp.adapter.AdapterCallback
import com.android.moviesapp.adapter.PagingItemMovieAdapter
import com.android.moviesapp.db.MovieDatabase
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.repository.MovieRepository
import com.android.moviesapp.swipe.SwipeHelper
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment(), AdapterCallback {

    companion object {
        private const val PREFS_NAME = "preference"
        private const val KEY_NAME = "query"
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PagingItemMovieAdapter
    private lateinit var query: String
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val repository = MovieRepository(MovieDatabase.getInstance(activity?.application!!).movieDao())
        val viewModelFactory = ViewModelFactory(repository, TypeModel.SEARCH)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        sharedPreference = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)!!
        query = sharedPreference.getString(KEY_NAME, "").toString()
        if (query.trim() != "") {
            viewModel.getSearchMovies(query)
        }

        swipeRefresh = root.findViewById(R.id.search_swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.getSearchMovies(query)
            swipeRefresh.isRefreshing = false
        }

        recyclerView = root.findViewById(R.id.search_recycler_view)
        adapter = PagingItemMovieAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.searchMovies.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        ItemTouchHelper(object: SwipeHelper(context, 0, ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                adapter.notifyItemChanged(position)

                val item = adapter.getItemMovie(position)
                val movie = item?.movie

                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.insert(movie)
                    if (item?.favorite!!) Toast.makeText(context, "Movie is already in favorites", Toast.LENGTH_SHORT).show()
                    else Snackbar.make(root.findViewById(R.id.search_snackbar), "Movie was added", Snackbar.LENGTH_LONG)
                            .setAction("Undo") { viewModel.delete(movie) }.show()
                }
            }
        }).apply { attachToRecyclerView(recyclerView) }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        val item = menu.findItem(R.id.search)
        val view = item.actionView as SearchView

        view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                view.clearFocus()
                item.collapseActionView()
                recyclerView.scrollToPosition(0)
                if (text != null) {
                    viewModel.getSearchMovies(text)
                    query = text
                    val editor = sharedPreference.edit()
                    editor?.putString(KEY_NAME, text)
                    editor?.apply()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }
        })
    }

    override fun onItemClicked(movie: Movie, favorite: Boolean) {
        val action = SearchFragmentDirections.actionNavigationSearchToNavigationMovie(movie, favorite)
        findNavController().navigate(action)
    }
}