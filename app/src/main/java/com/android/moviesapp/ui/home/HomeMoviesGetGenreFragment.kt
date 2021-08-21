package com.android.moviesapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.moviesapp.R
import com.android.moviesapp.databinding.FragmentHomeMoviesGetGenreBinding
import com.android.moviesapp.util.Constant
import com.android.moviesapp.util.Constant.Companion.BUNDLE_KEY_GENRE
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn
import java.util.*

class HomeMoviesGetGenreFragment : Fragment(R.layout.fragment_home_movies_get_genre) {

    private lateinit var binding: FragmentHomeMoviesGetGenreBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeMoviesGetGenreBinding.bind(view)
        navController = Navigation.findNavController(view)

        initList()
        initToolbar()
    }

    private fun initList() {
        binding.run {
            moviesGetGenreList.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                Constant.getNamesAllGenres(requireContext()).map { it.capitalize(Locale.ROOT) }
            )
            moviesGetGenreList.setOnItemClickListener { _, _, position, _ ->
                val genre = moviesGetGenreList.getItemAtPosition(position) as String
                val bundle = bundleOf(
                    BUNDLE_KEY_GENRE to Constant.getIdByGenre(requireContext(), genre)
                )
                navController.navigate(R.id.action_movies_get_genre_to_movies_by_genre, bundle)
            }
        }
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.moviesGetGenreToolbar)
            supportActionBar?.setHomeBtn(true)
        }
    }
}
