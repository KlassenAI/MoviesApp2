package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.MovieListItemAdapter.MovieViewHolder
import com.android.moviesapp.callback.AdapterCallback
import com.android.moviesapp.databinding.ListItemBinding
import com.android.moviesapp.model.Movie
import com.android.moviesapp.ui.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieListItemAdapter(
    private var movies: List<Movie>,
    private val viewModel: MainViewModel,
    private val callback: AdapterCallback
) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemBinding: ListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item,
                parent,
                false
            )
        return MovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie, position)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class MovieViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(movie: Movie, position: Int) {
                binding.movie = movie

                binding.run {

                    listItemCard.setOnClickListener {
                        callback.showMovieInfo(movie)
                    }

                    CoroutineScope(IO).launch {
                        val favorite = viewModel.isExist(movie.id)
                        withContext(Main) {
                            listItemLike.setImageResource(
                                if (favorite) R.drawable.ic_heart_fill_64
                                else R.drawable.ic_heart
                            )
                        }
                    }

                    listItemLike.setOnClickListener {
                        CoroutineScope(IO).launch {
                            val favorite = viewModel.isExist(movie.id)
                            withContext(Main) {
                                if (favorite) viewModel.delete(movie)
                                else viewModel.insert(movie)
                                notifyItemChanged(position)
                            }
                        }
                    }
                }
            }
        }

    fun setList(list: List<Movie>) {
        movies = list
        notifyDataSetChanged()
    }

    fun get(id: Int) = movies[id]
}
