package com.android.moviesapp.ui.watchlist

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.android.moviesapp.R
import com.android.moviesapp.callback.WatchListDialogCallback
import com.android.moviesapp.databinding.DialogWatchListBinding
import com.android.moviesapp.model.Column.*
import com.android.moviesapp.model.Sort
import com.android.moviesapp.model.Sort.*

class WatchListDialog(private val sort: Sort, private val callbackWatchList: WatchListDialogCallback) :
    DialogFragment(R.layout.dialog_watch_list), View.OnClickListener {

    private lateinit var binding: DialogWatchListBinding
    private var ascending: Boolean = sort.isAscending()
    private var checkedBtnId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWatchListBinding.bind(view)
        binding.run {
            setOnClickListener(
                watchListBtnAddDate,
                watchListBtnId,
                watchListBtnPopularity,
                watchListBtnReleaseDate,
                watchListBtnTitle,
                watchListBtnVoteAverage,
                watchListBtnVoteCount
            )
            when (sort.getColumn()) {
                ADD_DATE -> setCheckedRadioButton(watchListBtnAddDate)
                ID -> setCheckedRadioButton(watchListBtnId)
                POPULARITY -> setCheckedRadioButton(watchListBtnPopularity)
                RELEASE_DATE -> setCheckedRadioButton(watchListBtnReleaseDate)
                TITLE -> setCheckedRadioButton(watchListBtnTitle)
                VOTE_AVERAGE -> setCheckedRadioButton(watchListBtnVoteAverage)
                VOTE_COUNT -> setCheckedRadioButton(watchListBtnVoteCount)
            }
            watchListBtnYes.setOnClickListener {
                val sort = when (binding.watchListRadioGroup.checkedRadioButtonId) {
                    R.id.watch_list_btn_add_date -> if (ascending) ADD_DATE_ASC else ADD_DATE_DESC
                    R.id.watch_list_btn_id -> if (ascending) ID_ASC else ID_DESC
                    R.id.watch_list_btn_popularity -> if (ascending) POPULARITY_ASC else POPULARITY_DESC
                    R.id.watch_list_btn_release_date -> if (ascending) RELEASE_DATE_ASC else RELEASE_DATE_DESC
                    R.id.watch_list_btn_title -> if (ascending) TITLE_ASC else TITLE_DESC
                    R.id.watch_list_btn_vote_average -> if (ascending) VOTE_AVERAGE_ASC else VOTE_AVERAGE_DESC
                    R.id.watch_list_btn_vote_count -> if (ascending) VOTE_COUNT_ASC else VOTE_COUNT_DESC
                    else -> ADD_DATE_DESC
                }
                callbackWatchList.setSort(sort)
                dismiss()
            }
            watchListBtnNo.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setCheckedRadioButton(btn: RadioButton) {
        when {
            checkedBtnId == null -> {
                btn.isChecked = true
                checkedBtnId = btn.id
            }
            btn.id == checkedBtnId -> {
                ascending = !ascending
            }
            else -> {
                ascending = false
                checkedBtnId = btn.id
            }
        }
        btn.setDrawableToRight(getArrowImage(ascending))
    }

    private fun RadioButton.setDrawableToRight(r: Int) {
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, r, 0)
    }

    private fun setUncheckedRadioButton(vararg buttons: RadioButton) {
        buttons.forEach {
            it.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun setOnClickListener(vararg buttons: RadioButton) {
        buttons.forEach { it.setOnClickListener(this) }
    }

    private fun getArrowImage(ascending: Boolean) =
        if (ascending) R.drawable.ic_arrow_upward else R.drawable.ic_arrow_downward

    override fun onClick(v: View?) {
        if (v is RadioButton) {
            binding.run {
                setUncheckedRadioButton(
                    watchListBtnAddDate,
                    watchListBtnId,
                    watchListBtnPopularity,
                    watchListBtnReleaseDate,
                    watchListBtnTitle,
                    watchListBtnVoteAverage,
                    watchListBtnVoteCount
                )
            }
            setCheckedRadioButton(v)
        }
    }
}
