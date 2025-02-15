package com.example.beerdistrkt.fragPages.realisationtotal.presentation.model

data class UserSpinner(
    val id: String,
    val name: String,
) {
    companion object {
        val baseItem = UserSpinner("0", "ყველა")
    }
}
