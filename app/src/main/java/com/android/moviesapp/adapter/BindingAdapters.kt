package com.android.moviesapp.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.android.moviesapp.R
import com.android.moviesapp.util.Constant
import com.android.moviesapp.util.Constant.Companion.PATTERN_DAY_MONTH_YEAR
import com.android.moviesapp.util.Constant.Companion.PATTERN_YEAR_MONTH_DAY
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class BindingAdapters {

    companion object {

        @BindingAdapter("imagePath")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView.context)
                .load(getImagePath(imageUrl))
                .placeholder(getPlaceholder(imageView.context))
                .error(R.drawable.poster_error)
                .into(imageView)
        }

        private fun getImagePath(imageUrl: String) = "https://image.tmdb.org/t/p/w500$imageUrl"

        private fun getPlaceholder(context: Context) =
            CircularProgressDrawable(context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            }

        @BindingAdapter("releaseDate")
        @JvmStatic
        fun formatDate(textView: TextView, releaseDate: String) {
            if (releaseDate.isNotEmpty()) {
                textView.text = reformatStringDate(releaseDate)
            } else {
                textView.text = textView.getString(R.string.no_date)
            }
        }

        private fun reformatStringDate(stringDate: String) =
            stringDate.parseToDate(PATTERN_YEAR_MONTH_DAY).formatToString(PATTERN_DAY_MONTH_YEAR)

        private fun String.parseToDate(pattern: String) = getFormatter(pattern).parse(this)!!

        private fun Date.formatToString(pattern: String) = getFormatter(pattern).format(this)

        private fun getFormatter(pattern: String) = SimpleDateFormat(pattern, Locale.ROOT)

        @BindingAdapter("overview")
        @JvmStatic
        fun formatOverview(textView: TextView, overview: String) {
            if (overview.isNotEmpty()) {
                textView.text = overview
            } else {
                textView.text = textView.getString(R.string.no_overview)
            }
        }

        @BindingAdapter("genre")
        @JvmStatic
        fun formatGenre(textView: TextView, genres: List<Int>) {
            if (genres.isEmpty()) {
                textView.text = textView.getString(R.string.no_genre)
            } else {
                val genre = Constant.getGenreById(textView.context, genres[0])
                textView.text = genre.capitalize(Locale.ROOT)
            }
        }

        @BindingAdapter("double")
        @JvmStatic
        fun formatDouble(textView: TextView, number: Double) {
            textView.text = number.roundToInt().toString()
        }

        private fun View.getString(resId: Int) = this.context.getString(resId)
    }
}
