package com.example.beerdistrkt.fragPages.beer.data.model

import androidx.annotation.Keep

@Keep
data class BeerSortUpdateDto(
    val beerId: Int,
    val sortValue: Double,
)
