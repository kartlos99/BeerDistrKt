package com.example.beerdistrkt.fragPages.bottle.presentation

import androidx.annotation.StringRes
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle

sealed class Event {
    data class IncorrectDataEntered(@StringRes val msgID: Int) : Event()
    data object DataSaved : Event()
    data class ShowLoading(val isLoading: Boolean) : Event()
    data class Error(
        val code: Int,
        val error: String
    ) : Event()

    data class EditBottle(val bottle: Bottle) : Event()
}