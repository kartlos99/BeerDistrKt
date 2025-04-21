package com.example.beerdistrkt.fragPages.bottle.data.model

import androidx.annotation.Keep

@Keep
data class BottleOrderUpdateDto(
    val bottleId: Int,
    val sortValue: Double,
)
