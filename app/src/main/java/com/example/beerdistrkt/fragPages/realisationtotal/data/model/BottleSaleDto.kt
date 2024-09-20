package com.example.beerdistrkt.fragPages.realisationtotal.data.model

import androidx.annotation.Keep

@Keep
data class BottleSaleDto(
    val bottleID: Int,
    val name: String,
    val price: Double,
    val count: Int,
)
