package com.android.moviesapp.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.android.moviesapp.R
import com.android.moviesapp.util.Constant
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class BindingAdapters {

    companion object {

        @BindingAdapter("imagePath")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {

            val placeholder = CircularProgressDrawable(imageView.context)
            placeholder.strokeWidth = 5f
            placeholder.centerRadius = 30f
            placeholder.start()

            val imagePath: String = if (imageUrl != null) "https://image.tmdb.org/t/p/w500$imageUrl" else ""
            Glide.with(imageView.context)
                .load(imagePath)
                .placeholder(placeholder)
                .error(R.drawable.poster_error)
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
                textView.text = textView.context.getString(R.string.no_date)
            }
        }

        @BindingAdapter("overview")
        @JvmStatic
        fun formatOverview(textView: TextView, overview: String?) {
            if (overview != null && overview.isNotEmpty()) {
                textView.text = overview
            } else {
                textView.text = textView.context.getString(R.string.no_overview)
            }
        }

        @BindingAdapter("genre")
        @JvmStatic
        fun formatGenre(textView: TextView, genres: List<Int>) {
            if (genres.isEmpty()) {
                textView.text = textView.context.getString(R.string.no_genre)
            } else {
                val genre = Constant.getGenreById(textView.context, genres[0])
                textView.text = genre.capitalize(Locale.ROOT)
            }
        }

        @BindingAdapter("double")
        @JvmStatic
        fun formatDouble(textView: TextView, d: Double) {
            val text = d.roundToInt().toString()
            textView.text = text
        }
    }
}
