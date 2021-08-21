package com.android.moviesapp.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.moviesapp.R
import com.android.moviesapp.callback.WatchListDialogCallback
import com.android.moviesapp.databinding.DialogSetSortBinding
import com.android.moviesapp.databinding.DialogSetSortItemBinding
import com.android.moviesapp.enums.SortEnum
import com.android.moviesapp.model.SortItem
import com.android.moviesapp.ui.watchlist.SetSortDialog.WatchListDialogAdapter.WatchListDialogViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SetSortDialog(
    private val sortItem: SortItem,
    private val listener: WatchListDialogCallback
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSetSortBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_set_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = DialogSetSortBinding.bind(view)
        initSortList()
    }

    private fun initSortList() {
        binding.watchListDialogList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = WatchListDialogAdapter(SortEnum.values())
        }
    }

    private inner class WatchListDialogAdapter(
        private val sortEnums: Array<SortEnum>
    ) : RecyclerView.Adapter<WatchListDialogViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): WatchListDialogViewHolder {
            val itemBinding: DialogSetSortItemBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.dialog_set_sort_item,
                    parent,
                    false
                )
            return WatchListDialogViewHolder(itemBinding)
        }

        override fun onBindViewHolder(
            holderWatchListDialog: WatchListDialogViewHolder, position: Int
        ) {
            holderWatchListDialog.bind(sortEnums[position])
        }

        override fun getItemCount(): Int {
            return sortEnums.size
        }

        private inner class WatchListDialogViewHolder(
            val binding: DialogSetSortItemBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(sortEnum: SortEnum) {
                binding.apply {
                    watchListDialogItemText.text = sortEnum.getSortName(requireContext())

                    if (sortEnum == sortItem.sortEnum) {
                        watchListDialogItemImage.apply {
                            isVisible = true
                            if (sortItem.ascending) setImageResource(R.drawable.ic_arrow_upward)
                            else setImageResource(R.drawable.ic_arrow_downward)
                        }
                    }

                    watchListDialogItemCard.setOnClickListener {
                        if (sortEnum == sortItem.sortEnum) {
                            sortItem.reverseOrder()
                            listener.setSortList(sortItem)
                        } else {
                            listener.setSortList(SortItem(sortEnum))
                        }
                        dismiss()
                    }
                }
            }
        }
    }
}