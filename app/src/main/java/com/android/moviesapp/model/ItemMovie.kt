package com.android.moviesapp.model

import android.os.Parcel
import android.os.Parcelable

class ItemMovie(val movie: Movie?, var favorite: Boolean) {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Movie::class.java.classLoader),
            parcel.readByte() != 0.toByte()) {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemMovie

        if (movie != other.movie) return false
        if (favorite != other.favorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movie?.hashCode() ?: 0
        result = 31 * result + favorite.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<ItemMovie> {
        override fun createFromParcel(parcel: Parcel): ItemMovie {
            return ItemMovie(parcel)
        }

        override fun newArray(size: Int): Array<ItemMovie?> {
            return arrayOfNulls(size)
        }
    }
}