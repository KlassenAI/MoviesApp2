package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.MovieListItemAdapter.MovieViewHolder
import com.android.moviesapp.databinding.MovieListItemBinding
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.model.TypeModel.*
import com.android.moviesapp.model.Movie

class MovieListItemAdapter(
        private var movies: ArrayList<Movie>?, 
        private val view: View, 
        private val typeModel: TypeModel
) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemBinding: MovieListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.movie_list_item,
                parent,
                false
            )
        return MovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies!![position]
        holder.binding.movie = movie

        holder.binding.root.setOnClickListener {
            val bundle = bundleOf("movie" to movie)
            if (typeModel == WISHLIST) Navigation.findNavController(view).navigate(R.id.action_navigation_wishlist_to_movieFragment, bundleOf("movie" to movie))
        }
    }

    override fun getItemCount(): Int {
        return if (movies == null) 0 else movies!!.size
    }

    inner class MovieViewHolder(val binding: MovieListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setList(list: ArrayList<Movie>) {
        movies = list
        notifyDataSetChanged()
    }

    fun getMovie(position: Int) : Movie? {
        return movies?.get(position)
    }
}