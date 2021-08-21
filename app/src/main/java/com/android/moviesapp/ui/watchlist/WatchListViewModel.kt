package com.android.moviesapp.ui.watchlist

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.android.moviesapp.model.SortItem
import com.android.moviesapp.ui.main.MainViewModel
import com.google.gson.Gson

class WatchListViewModel(application: Application) : MainViewModel(application) {

    companion object {
        private const val PREF_SORT_NAME = "sort"
        private const val PREF_SORT_DEF_VALUE = ""

        private val PREF_SORT_KEY = SortItem::class.java.simpleName
    }

    private val sharedPreferences: SharedPreferences = application.applicationContext
        .getSharedPreferences(PREF_SORT_NAME, Context.MODE_PRIVATE)


    val currentSort = MutableLiveData(getSharedPreferenceSort())

    val favoriteMovies = currentSort.switchMap {
        repository.getFavoriteMovies(it)
    }

    fun getFavorites(sortItem: SortItem) {
        currentSort.postValue(sortItem)
        saveSharedPreferences(sortItem)
    }

    private fun saveSharedPreferences(sortItem: SortItem) {
        val editor = sharedPreferences.edit()
        editor.saveSort(sortItem)
        editor.apply()
    }

    private fun SharedPreferences.Editor.saveSort(sortItem: SortItem) {
        val json = Gson().toJson(sortItem)
        putString(PREF_SORT_KEY, json)
    }

    private fun getSharedPreferenceSort(): SortItem {
        val json = sharedPreferences.getString(PREF_SORT_KEY, PREF_SORT_DEF_VALUE)
        if (json == PREF_SORT_DEF_VALUE) return SortItem()
        return Gson().fromJson(json, SortItem::class.java)
    }
}
