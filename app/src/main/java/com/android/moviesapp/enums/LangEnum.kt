package com.android.moviesapp.enums

enum class LangEnum {
    RU,
    EN;

    companion object {
        fun get(language: String) = when(language) {
            "en" -> EN
            else -> RU
        }
    }


    fun getLanguage() =
        when(this) {
            RU -> "ru"
            EN -> "en"
        }

    fun getRegion() =
        when(this) {
            RU -> "ru"
            EN -> "us"
        }
}