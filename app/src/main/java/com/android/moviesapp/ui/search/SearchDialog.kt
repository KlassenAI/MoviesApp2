package com.android.moviesapp.ui.search

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.android.moviesapp.R
import com.android.moviesapp.callback.SearchDialogCallback
import com.android.moviesapp.databinding.DialogSearchBinding
import java.util.*

class SearchDialog(
    private val callback: SearchDialogCallback,
    private val isRangePicker: Boolean,
    private val year: String? = null,
    private val range: List<String>? = null
) : DialogFragment(R.layout.dialog_search) {

    private lateinit var binding: DialogSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSearchBinding.bind(view)
        binding.run {
            if (isRangePicker) {
                if (range != null) {
                    searchPickerFrom.setYears(range[0])
                    searchPickerTo.setYears(range[1])
                } else {
                    searchPickerFrom.setYears()
                    searchPickerTo.setYears()
                }
            } else {
                if (year != null) {
                    searchPickerTo.setYears(year)
                } else {
                    searchPickerTo.setYears()
                }
                searchPickerFrom.isVisible = false
                searchPickerText.isVisible = false
            }

            searchPickerFrom.setOnValueChangedListener { picker, oldVal, newVal ->
                searchPickerTo.minValue = newVal
            }

            searchBtnOk.setOnClickListener {
                if (isRangePicker) {
                    callback.setYears(searchPickerFrom.value, searchPickerTo.value)
                } else {
                    callback.setYears(searchPickerTo.value, searchPickerTo.value)
                }
                dismiss()
            }
        }
    }

    private fun NumberPicker.setYears(year: String? = null) {
        val calendar = Calendar.getInstance()
        val calendarYear = calendar.get(Calendar.YEAR)
        minValue = getString(R.string.min_year).toInt()
        maxValue = calendarYear +  getString(R.string.next_year_space).toInt()
        value = year?.toInt() ?: calendarYear
    }
}