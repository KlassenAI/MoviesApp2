package com.android.moviesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.databinding.LoadStateFooterBinding
import com.android.moviesapp.adapter.MovieLoadStateAdapter.LoadStateViewHolder

class MovieLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.binding.footerBtn.setOnClickListener {
            retry.invoke()
        }
        holder.binding.apply {
            footerProgressBar.isVisible = loadState is LoadState.Loading
            footerBtn.isVisible = loadState !is LoadState.Loading
            footerTextError.isVisible = loadState !is LoadState.Loading
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding)
    }

    inner class LoadStateViewHolder(val binding: LoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root)

}