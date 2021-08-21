package com.android.moviesapp.ui.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.moviesapp.R
import com.android.moviesapp.callback.SearchDialogCallback
import com.android.moviesapp.databinding.FragmentSearchGetFilterBinding
import com.android.moviesapp.util.Constant
import com.android.moviesapp.util.Constant.Companion.KEY_GENRE
import com.android.moviesapp.util.Constant.Companion.KEY_RELEASE_DATE_LESS
import com.android.moviesapp.util.Constant.Companion.KEY_RELEASE_DATE_MORE
import com.android.moviesapp.util.Constant.Companion.KEY_RUNTIME_LESS
import com.android.moviesapp.util.Constant.Companion.KEY_RUNTIME_MORE
import com.android.moviesapp.util.Constant.Companion.KEY_SORT_BY
import com.android.moviesapp.util.Constant.Companion.KEY_VOTE_AVERAGE
import com.android.moviesapp.util.Constant.Companion.KEY_VOTE_COUNT
import com.android.moviesapp.util.Constant.Companion.KEY_YEAR
import com.android.moviesapp.util.Expansions.Companion.setHomeBtn
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchGetFilterFragment : Fragment(R.layout.fragment_search_get_filter),
    SearchDialogCallback {

    companion object {
        private const val PLUS = "+"
    }

    private lateinit var binding: FragmentSearchGetFilterBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchGetFilterBinding.bind(view)
        navController = Navigation.findNavController(view)
        initFields()
    }

    override fun onStart() {
        super.onStart()
        binding.run {
            /*
             * Если после поиска фильмов по фильму в SearchByFilterFragment нажать назад и вернуться
             * обратно, то некоторые элементы могут быть checked, поэтому они отключаются
             */
            searchChipYear.apply { isChecked = tag != null }
            searchChipRange.apply { isChecked = tag != null }
        }
    }

    private fun initFields() {
        initBtnFilterSearch()
        initToolbar()
        initRateChips()
        initCountChips()
        initGenreChips()
        initDateChips()
        initRuntimeChips()
        initSortChips()
    }

    private fun initBtnFilterSearch() {
        binding.btnFilterSearch.setOnClickListener {
            val bundle = bundleOf(HashMap::class.java.simpleName to getFilterMap())
            navController.navigate(R.id.action_search_get_filter_to_search_by_filter, bundle)
        }
    }

    private fun getFilterMap(): HashMap<String, String> {
        val map: HashMap<String, String> = hashMapOf()

        // todo Вынести в отдельные функции, ибо не понятно, что делает этот огромный кусок кода
        binding.apply {
            rateChips.apply {
                val chip: Chip? = findViewById(checkedChipId)
                if (chip != null) {
                    map[KEY_VOTE_AVERAGE] = chip.tag.toString()
                }
            }
            countChips.apply {
                val chip: Chip? = findViewById(checkedChipId)
                if (chip != null) {
                    map[KEY_VOTE_COUNT] = chip.tag.toString()
                }
            }
            genreChips.apply {
                val genreArrayList = arrayListOf<String>()
                checkedChipIds.forEach {
                    val chip: Chip = findViewById(it)
                    genreArrayList.add(Constant.getIdByGenre(context, chip.text.toString()))
                }
                if (genreArrayList.isNotEmpty()) {
                    val genres = genreArrayList.joinToString(",")
                    map[KEY_GENRE] = genres
                }
            }
            runtimeChips.apply {
                val chip: Chip? = findViewById(checkedChipId)
                if (chip != null) {
                    when (chip.text) {
                        getString(R.string.less_hour) -> {
                            map[KEY_RUNTIME_LESS] = getString(R.string.runtime_60)
                        }
                        getString(R.string.one_two_hours) -> {
                            map[KEY_RUNTIME_MORE] = getString(R.string.runtime_60)
                            map[KEY_RUNTIME_LESS] = getString(R.string.runtime_120)
                        }
                        getString(R.string.two_three_hours) -> {
                            map[KEY_RUNTIME_MORE] = getString(R.string.runtime_120)
                            map[KEY_RUNTIME_LESS] = getString(R.string.runtime_180)
                        }
                        getString(R.string.more_three_hours) -> {
                            map[KEY_RUNTIME_MORE] = getString(R.string.runtime_180)
                        }
                    }
                }
            }
            dateChips.apply {
                val chipYear: Chip = findViewById(binding.searchChipYear.id)
                val chipRange: Chip = findViewById(binding.searchChipRange.id)
                if (chipYear.tag != null) {
                    map[KEY_YEAR] = chipYear.tag.toString()
                } else if (chipRange.tag != null) {
                    val range = chipRange.tag.toString().split(" ")
                    map[KEY_RELEASE_DATE_MORE] = range[0] + "-01-01"
                    map[KEY_RELEASE_DATE_LESS] = range[1] + "-12-31"
                }
            }
            sortChips.apply {
                val chip: Chip? = findViewById(checkedChipId)
                if (chip != null) {
                    val ascending = chip.tag.equals("true")
                    Constant.getSortValue(context, chip.text.toString(), ascending)?.let {
                        map[KEY_SORT_BY] = it
                    }
                }
            }
        }

        return map
    }

    private fun initRateChips() {
        binding.rateChips.run {
            val rates = Constant.getRateList(requireContext())
            rates.forEach { addView(createChip("$it$PLUS", tag = it)) }
        }
    }

    private fun initCountChips() {
        binding.countChips.run {
            val counts = Constant.getCountList(requireContext())
            counts.forEach { addView(createChip("$it$PLUS", tag = it)) }
        }
    }

    private fun initGenreChips() {
        binding.genreChips.run {
            val genres = Constant.getNamesAllGenres(requireContext())
            genres.forEach { addView(createChip(it)) }
        }
    }

    private fun initRuntimeChips() {
        binding.runtimeChips.run {
            val runtime = Constant.getRuntimeList(requireContext())
            runtime.forEach { addView(createChip(it)) }
        }
    }

    private fun initDateChips() {
        binding.run {
            searchChipYear.run {
                setOnClickListener {
                    isChecked = tag != null
                    val year: String? = if (tag != null) tag as String else null
                    showDialog(false, year = year)
                }
            }
            searchChipRange.run {
                setOnClickListener {
                    isChecked = tag != null
                    val range: List<String>? =
                        if (tag != null) (tag as String).split(" ") else null
                    showDialog(true, range = range)
                }
            }
            searchChipChangeDate.setOnClickListener {
                val chipYear: Chip = binding.searchChipYear
                if (chipYear.tag != null) {
                    searchChipRange.run {
                        isChecked = false
                        val range: List<String>? =
                            if (tag != null) (tag as String).split(" ") else null
                        showDialog(true, range = range)
                    }
                } else {
                    searchChipYear.run {
                        isChecked = false
                        val year: String? = if (tag != null) tag as String else null
                        showDialog(false, year = year)
                    }
                }
            }
            searchChipClearDate.setOnClickListener {
                searchChipYear.run {
                    isVisible = true
                    text = getString(R.string.set_year)
                    tag = null
                    isChecked = false
                }
                searchChipRange.run {
                    isVisible = true
                    text = getString(R.string.set_range)
                    tag = null
                    isChecked = false
                }
                searchChipChangeDate.isVisible = false
                searchChipClearDate.isVisible = false
            }
        }
    }

    override fun setYears(from: Int, to: Int) {
        binding.run {
            if (from == to) {
                searchChipYear.run {
                    text = from.toString()
                    tag = from.toString()
                    isChecked = true
                }
                searchChipRange.tag = null
                searchChipChangeDate.text = getString(R.string.change_to_range)
            } else {
                searchChipRange.run {
                    text = getString(R.string.format_range_date_text).format(from.toString(), to.toString())
                    tag = getString(R.string.format_range_date_tag).format(from.toString(), to.toString())
                    isChecked = true
                }
                searchChipYear.tag = null
                searchChipChangeDate.text = getString(R.string.change_to_year)
            }
            searchChipYear.isVisible = from == to
            searchChipRange.isVisible = from != to
            searchChipChangeDate.isVisible = true
            searchChipClearDate.isVisible = true
        }
    }

    private fun initSortChips() {
        binding.sortChips.run {
            val sorts = Constant.getSortList(requireContext())
            sorts.forEach {
                addView(createChip(it, tag = "true", draw = getAscendingImage(true)))
                addView(createChip(it, tag = "false", draw = getAscendingImage(false)))
            }
        }
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(binding.searchAdvanceToolbar)
            supportActionBar?.setHomeBtn(true)
        }
    }

    private fun showDialog(
        isRangePicker: Boolean,
        year: String? = null,
        range: List<String>? = null
    ) {
        val dialog = SetYearsDialog(this, isRangePicker, year = year, range = range)
        dialog.dialog?.window?.setLayout(R.dimen.dialog_search_width, R.dimen.dialog_search_height)
        dialog.show(childFragmentManager, "dialog")
    }

    private fun ChipGroup.createChip(text: String, tag: String? = null, draw: Int? = null): Chip {
        val chip = layoutInflater.inflate(R.layout.chip_choice, this, false) as Chip
        chip.text = text
        chip.tag = tag
        draw?.let { chip.setChipIconResource(it) }
        return chip
    }

    private fun getAscendingImage(ascending: Boolean) =
        if (ascending) R.drawable.ic_arrow_upward else R.drawable.ic_arrow_downward
}
