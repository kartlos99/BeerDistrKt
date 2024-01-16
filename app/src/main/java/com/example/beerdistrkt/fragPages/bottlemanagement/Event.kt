package com.example.beerdistrkt.fragPages.bottlemanagement

import androidx.annotation.StringRes
import com.example.beerdistrkt.models.bottle.BaseBottleModel

sealed class Event {
    data class IncorrectDataEntered(@StringRes val msgID: Int) : Event()
    object DataSaved : Event()
    data class ShowLoading(val isLoading: Boolean) : Event()
    data class Error(
        val code: Int,
        val error: String
    ) : Event()

    data class EditBottle(val bottle: BaseBottleModel) : Event()
}