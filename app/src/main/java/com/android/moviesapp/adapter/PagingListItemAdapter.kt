package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.PagingListItemAdapter.PagingItemMovieViewHolder
import com.android.moviesapp.callback.AdapterCallback
import com.android.moviesapp.databinding.ListItemBinding
import com.android.moviesapp.model.Movie
import com.android.moviesapp.ui.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PagingListItemAdapter(
    private val viewModel: MainViewModel,
    private val callback: AdapterCallback
) : PagingDataAdapter<Movie, PagingItemMovieViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Movie> =
            object : DiffUtil.ItemCallback<Movie>() {
                override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingItemMovieViewHolder {
        val itemBinding: ListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.list_item, parent, false
            )
        return PagingItemMovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PagingItemMovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie, position)
    }

    inner class PagingItemMovieViewHolder(
        val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie?, position: Int) {
            binding.movie = movie
            binding.run {

                listItemCard.setOnClickListener {
                    callback.showMovieInfo(movie!!)
                }

                CoroutineScope(IO).launch {
                    val favorite = viewModel.isExist(movie?.id!!)
                    withContext(Main) {
                        listItemLike.setImageResource(
                            if (favorite) R.drawable.ic_heart_fill_64
                            else R.drawable.ic_heart
                        )
                    }
                }

                listItemLike.setOnClickListener {
                    CoroutineScope(IO).launch {
                        val favorite = viewModel.isExist(movie?.id!!)
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

    fun get(position: Int) = getItem(position)
}
