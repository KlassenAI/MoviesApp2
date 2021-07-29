package com.android.moviesapp.model

import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.android.moviesapp.R
import com.android.moviesapp.util.Constants
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*


class BindingAdapters {

    companion object {

        @BindingAdapter("imagePath")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {

            val placeholder = CircularProgressDrawable(imageView.context)
            placeholder.setColorSchemeColors(R.color.colorPrimaryLight, R.color.colorPrimaryVariantLight)
            placeholder.strokeWidth = 5f
            placeholder.centerRadius = 30f
            placeholder.start()

            val imagePath: String = if (imageUrl != null) "https://image.tmdb.org/t/p/w500$imageUrl" else ""
            Glide.with(imageView.context)
                .load(imagePath)
                .placeholder(placeholder)
                .error(R.drawable.default_poster)
                .into(imageView)
        }

        @BindingAdapter("releaseDate")
        @JvmStatic
        fun formatDate(textView: TextView, releaseDate: String?) {
            if (releaseDate != null && releaseDate.isNotEmpty()) {
                var df = SimpleDateFormat("y-MM-dd", Locale("ru"))
                val date = df.parse(releaseDate)!!
                df = SimpleDateFormat("d MMMM y", Locale("ru"))
                textView.text = df.format(date)
            } else {
                textView.text = "Нет даты"
            }
        }

        @BindingAdapter("releaseShortDate")
        @JvmStatic
        fun formatShortDate(textView: TextView, releaseDate: String?) {
            if (releaseDate != null && releaseDate.isNotEmpty()) {
                var df = SimpleDateFormat("y-MM-dd", Locale("ru"))
                val date = df.parse(releaseDate)!!
                df = SimpleDateFormat("d MMM y", Locale("ru"))
                textView.text = df.format(date)
            } else {
                textView.text = "Нет даты"
            }
        }

        @BindingAdapter("genre")
        @JvmStatic
        fun formatGenre(textView: TextView, genres: List<Int>) {
            val map = Constants.genreMap
            val text = if (genres.isNotEmpty()) map[genres[0]].toString() else ""
            if (text.isEmpty()) textView.visibility = GONE
            else textView.text = text
        }
    }
}
