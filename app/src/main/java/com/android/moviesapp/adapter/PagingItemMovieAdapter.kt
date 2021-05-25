package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.PagingItemMovieAdapter.PagingItemMovieViewHolder
import com.android.moviesapp.databinding.ItemMovieListItemBinding
import com.android.moviesapp.model.ItemMovie
import com.android.moviesapp.model.Movie

class PagingItemMovieAdapter(
        private val callback: AdapterCallback
) : PagingDataAdapter<ItemMovie, PagingItemMovieViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<ItemMovie> = object : DiffUtil.ItemCallback<ItemMovie>() {
            override fun areItemsTheSame(oldItem: ItemMovie, newItem: ItemMovie) = oldItem.movie == newItem.movie
            override fun areContentsTheSame(oldItem: ItemMovie, newItem: ItemMovie) =  oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingItemMovieViewHolder {
        val itemBinding: ItemMovieListItemBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_movie_list_item,
                        parent,
                        false
                )
        return PagingItemMovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(viewHolderItem: PagingItemMovieViewHolder, position: Int) {
        val item = getItem(position)
        viewHolderItem.binding.item = item
        viewHolderItem.binding.root.setOnClickListener {
            callback.onItemClicked(item?.movie!!, item.favorite)
        }
    }

    fun getItemMovie(position: Int): ItemMovie? {
        return getItem(position)
    }

    class PagingItemMovieViewHolder(val binding: ItemMovieListItemBinding) :
            RecyclerView.ViewHolder(binding.root)
}

interface AdapterCallback {
    fun onItemClicked(movie: Movie, favorite: Boolean)
}