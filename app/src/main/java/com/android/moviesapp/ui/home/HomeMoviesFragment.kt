package com.android.moviesapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.AdapterCallback
import com.android.moviesapp.adapter.PagingItemMovieAdapter
import com.android.moviesapp.db.MovieDatabase
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.model.TypeResponse
import com.android.moviesapp.model.TypeResponse.*
import com.android.moviesapp.repository.MovieRepository
import com.android.moviesapp.swipe.SwipeHelper
import com.google.android.material.snackbar.Snackbar


class HomeMoviesFragment : Fragment(), AdapterCallback {

    private lateinit var viewModel: HomeMoviesViewModel

    private lateinit var adapter: PagingItemMovieAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val repository = MovieRepository(MovieDatabase.getInstance(activity?.application!!).movieDao())
        val viewModelFactory = ViewModelFactory(repository, TypeModel.HOME_MOVIES)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeMoviesViewModel::class.java)
        val root = inflater.inflate(R.layout.home_movies_fragment, container, false)

        val response = arguments?.get("response") as TypeResponse

        recyclerView = root.findViewById(R.id.movies_recycler_view)
        adapter = PagingItemMovieAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        when(response) {
            POPULAR -> {
                viewModel.popularMovies.observe(viewLifecycleOwner) {
                    adapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
            UPCOMING -> {
                viewModel.upcomingMovies.observe(viewLifecycleOwner) {
                    adapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
            TOPRATED -> {
                viewModel.topRatedMovies.observe(viewLifecycleOwner) {
                    adapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
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
                    else Snackbar.make(root.findViewById(R.id.movies_snackbar), "Movie was added", Snackbar.LENGTH_LONG)
                            .setAction("Undo") { viewModel.delete(movie) }.show()
                }
            }
        }).apply { attachToRecyclerView(recyclerView) }

        return root
    }

    override fun onItemClicked(movie: Movie, favorite: Boolean) {
        val action = HomeMoviesFragmentDirections.actionNavigationHomeMoviesToNavigationMovie(movie, favorite)
        findNavController().navigate(action)
    }
}