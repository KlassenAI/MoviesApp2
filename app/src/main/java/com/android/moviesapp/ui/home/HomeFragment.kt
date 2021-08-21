package com.android.moviesapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.moviesapp.R
import com.android.moviesapp.databinding.FragmentHomeBinding
import com.android.moviesapp.model.TypeRequest
import com.android.moviesapp.model.TypeRequest.*
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn

class HomeFragment : Fragment(R.layout.fragment_home), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        binding = FragmentHomeBinding.bind(view)

        initButtons()
        initToolbar()
    }

    private fun initButtons() {
        binding.apply {
            btnPopular.setOnClickListener(this@HomeFragment)
            btnUpcoming.setOnClickListener(this@HomeFragment)
            btnTop.setOnClickListener(this@HomeFragment)
            btnByGenre.setOnClickListener(this@HomeFragment)
        }
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.homeToolbar)
            supportActionBar?.setHomeBtn(false)
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_by_genre) {
            navController.navigate(R.id.action_home_to_movies_get_genre)
        } else {
            val bundle = Bundle()
            when (v?.id) {
                R.id.btn_popular -> bundle.putSerializable(TypeRequest::class.java.simpleName, POPULAR)
                R.id.btn_upcoming -> bundle.putSerializable(TypeRequest::class.java.simpleName, UPCOMING)
                R.id.btn_top -> bundle.putSerializable(TypeRequest::class.java.simpleName, TOP)
            }
            navController.navigate(R.id.action_home_to_movies, bundle)
        }
    }
}