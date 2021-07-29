package com.android.moviesapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.moviesapp.R
import com.android.moviesapp.databinding.FragmentHomeBinding
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.model.TypeRequest.*

class HomeFragment : Fragment(R.layout.fragment_home), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding = FragmentHomeBinding.bind(view)
        initFields()
    }

    private fun initFields() {
        initButtons()
    }

    private fun initButtons() {
        binding.also {
            it.btnPopular.setOnClickListener(this)
            it.btnUpcoming.setOnClickListener(this)
            it.btnTop.setOnClickListener(this)
            it.btnGenres.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        when (v?.id) {
            R.id.btn_popular -> bundle.putSerializable(TypeRequest::class.java.simpleName, POPULAR)
            R.id.btn_upcoming -> bundle.putSerializable(TypeRequest::class.java.simpleName, UPCOMING)
            R.id.btn_top -> bundle.putSerializable(TypeRequest::class.java.simpleName, TOP)
        }
        if (v?.id == R.id.btn_genres) {
            navController.navigate(R.id.action_home_to_movies_by_genre, bundle)
        } else {
            navController.navigate(R.id.action_home_to_movies, bundle)
        }
    }
}