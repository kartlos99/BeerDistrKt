package com.example.beerdistrkt.fragPages.realisationtotal.presentation.model

data class SpinnerStateModel(
    val items: List<UserSpinner> = listOf(UserSpinner.baseItem),
    val selectedPosition: Int = 0,
    val isBlocked: Boolean = false,
) {
    val names: List<String>
        get() = items.map { it.name }
}
