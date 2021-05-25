package com.android.moviesapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.MovieCardItemAdapter
import com.android.moviesapp.db.MovieDatabase
import com.android.moviesapp.factory.ViewModelFactory
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.model.Movie
import com.android.moviesapp.model.TypeResponse
import com.android.moviesapp.repository.MovieRepository

class HomeFragment : Fragment() {

    private var viewModel: HomeViewModel? = null

    private var popularMoviesRecyclerView: RecyclerView? = null
    private var upcomingMoviesRecyclerView: RecyclerView? = null
    private var topRatedMoviesRecyclerView: RecyclerView? = null

    private var popularMoviesAdapter: MovieCardItemAdapter? = null
    private var upcomingMoviesAdapter: MovieCardItemAdapter? = null
    private var topRatedMoviesAdapter: MovieCardItemAdapter? = null

    private var popularTextView: TextView? = null
    private var upcomingTextView: TextView? = null
    private var topRatedTextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        val repository = MovieRepository(MovieDatabase.getInstance(activity?.application!!).movieDao())
        val viewModelFactory = ViewModelFactory(repository, TypeModel.HOME)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        popularMoviesRecyclerView = root.findViewById(R.id.home_popular_recycler_view)
        popularMoviesAdapter = context?.let { MovieCardItemAdapter(ArrayList(), root, TypeModel.HOME) }
        popularMoviesRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        popularMoviesRecyclerView?.adapter = popularMoviesAdapter

        upcomingMoviesRecyclerView = root.findViewById(R.id.home_upcoming_recycler_view)
        upcomingMoviesAdapter = context?.let { MovieCardItemAdapter(ArrayList(), root, TypeModel.HOME) }
        upcomingMoviesRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        upcomingMoviesRecyclerView?.adapter = upcomingMoviesAdapter

        topRatedMoviesRecyclerView = root.findViewById(R.id.home_top_rated_recycler_view)
        topRatedMoviesAdapter = context?.let { MovieCardItemAdapter(ArrayList(), root, TypeModel.HOME) }
        topRatedMoviesRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        topRatedMoviesRecyclerView?.adapter = topRatedMoviesAdapter

        viewModel?.popularMovies?.observe(
            viewLifecycleOwner, Observer { popularMoviesAdapter?.setList(it) }
        )
        viewModel?.upcomingMovies?.observe(
            viewLifecycleOwner, Observer { upcomingMoviesAdapter?.setList(it) }
        )
        viewModel?.topRatedMovies?.observe(
            viewLifecycleOwner, Observer { topRatedMoviesAdapter?.setList(it) }
        )

        viewModel?.getPopularMovies()
        viewModel?.getUpcomingMovies()
        viewModel?.getTopRatedMovies()

        popularTextView = root.findViewById(R.id.popularViewAllTextView)
        popularTextView?.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationHomeMovies(TypeResponse.POPULAR)
            findNavController().navigate(action)
        }

        upcomingTextView = root.findViewById(R.id.upcomingViewAllTextView)
        upcomingTextView?.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationHomeMovies(TypeResponse.UPCOMING)
            findNavController().navigate(action)
        }

        topRatedTextView = root.findViewById(R.id.topRatedViewAllTextView)
        topRatedTextView?.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationHomeMovies(TypeResponse.TOPRATED)
            findNavController().navigate(action)
        }

        return root
    }
}