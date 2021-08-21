package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.adapter.GenreAdapter.GenreViewHolder
import com.android.moviesapp.databinding.GenreItemBinding

class GenreAdapter(private val genres: ArrayList<String>): RecyclerView.Adapter<GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val itemBinding: GenreItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.genre_item, parent, false
            )
        return GenreViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.binding.genreItem.text = genre
    }

    override fun getItemCount(): Int = genres.size

    inner class GenreViewHolder(val binding: GenreItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}