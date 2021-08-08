package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.adapter.MovieLoadStateAdapter.LoadStateViewHolder
import com.android.moviesapp.databinding.LoadStateBinding

class MovieLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.binding.loadStateBtnRepeat.setOnClickListener {
            retry.invoke()
        }
        holder.binding.apply {
            loadStateProgress.isVisible = loadState is LoadState.Loading
            loadStateBtnRepeat.isVisible = loadState !is LoadState.Loading
            loadStateText.isVisible = loadState !is LoadState.Loading
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LoadStateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding)
    }

    inner class LoadStateViewHolder(val binding: LoadStateBinding) :
        RecyclerView.ViewHolder(binding.root)

}