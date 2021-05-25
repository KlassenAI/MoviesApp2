package com.android.moviesapp.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.moviesapp.R
import com.android.moviesapp.adapter.MovieListItemAdapter
import com.android.moviesapp.db.MovieDatabase
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.model.Movie
import com.android.moviesapp.repository.MovieRepository
import com.android.moviesapp.swipe.SwipeHelper
import com.google.android.material.snackbar.Snackbar

class WishlistFragment : Fragment() {

    private lateinit var viewModel: WishlistViewModel
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieListItemAdapter

    override fun onCreateView(inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val repository = MovieRepository(MovieDatabase.getInstance(activity?.application!!).movieDao())

        val viewModelFactory = ViewModelFactory(repository, TypeModel.WISHLIST)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WishlistViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_wishlist, container, false)

        swipeRefresh = root.findViewById(R.id.wishlist_swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.favoritesMovies
            swipeRefresh.isRefreshing = false
        }

        recyclerView = root.findViewById(R.id.wishlist_recycler_view)
        adapter = MovieListItemAdapter(ArrayList(), root, TypeModel.WISHLIST)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.favoritesMovies.observe(
            viewLifecycleOwner, Observer { adapter.setList(it as ArrayList<Movie>) }
        )

        ItemTouchHelper(object: SwipeHelper(context, 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                adapter.notifyItemChanged(position)
                val movie = adapter.getMovie(position)

                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.delete(movie)
                        Snackbar.make(root.findViewById(R.id.wishlist_snackbar),
                                "Movie was deleted", Snackbar.LENGTH_LONG)
                                .setAction("Undo") { viewModel.insert(movie) }.show()
                    }
                }
            }
        }).apply { attachToRecyclerView(recyclerView) }

        return root
    }
}