package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.databinding.MovieCardItemBinding
import com.android.moviesapp.model.Movie
import com.android.moviesapp.adapter.MovieCardItemAdapter.MovieViewHolder
import com.android.moviesapp.model.TypeModel
import com.android.moviesapp.model.TypeModel.*

class MovieCardItemAdapter(
        private var movies: List<Movie>?,
        private val view: View,
        private val typeModel: TypeModel
) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemBinding: MovieCardItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.movie_card_item,
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
            if (typeModel == HOME) Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_movieFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return if (movies == null) 0 else movies!!.size
    }

    inner class MovieViewHolder(val binding: MovieCardItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setList(list: List<Movie>) {
        movies = list
        notifyDataSetChanged()
    }
}
