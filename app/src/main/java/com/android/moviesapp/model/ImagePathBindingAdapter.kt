package com.android.moviesapp.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.android.moviesapp.R
import com.bumptech.glide.Glide

class ImagePathBindingAdapter {
    companion object {
        @BindingAdapter("posterPath")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            val imagePath: String = if (imageUrl != null) "https://image.tmdb.org/t/p/w500$imageUrl" else ""
            Glide.with(imageView.context)
                .load(imagePath)
                .placeholder(R.drawable.noposter)
                .error(R.drawable.noposter)
                .into(imageView)
        }

        @BindingAdapter("backdropPath")
        @JvmStatic
        fun loadBackdrop(imageView: ImageView, imageUrl: String?) {
            val imagePath: String = if (imageUrl != null) "https://image.tmdb.org/t/p/w500$imageUrl" else ""
            Glide.with(imageView.context)
                    .load(imagePath)
                    .placeholder(R.color.design_default_color_background)
                    .error(R.drawable.noposter)
                    .into(imageView)
        }
    }
}
