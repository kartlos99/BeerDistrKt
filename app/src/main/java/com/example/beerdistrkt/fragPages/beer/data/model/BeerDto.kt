package com.example.beerdistrkt.fragPages.beer.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.models.BeerStatus

@Keep
data class BeerDto(
    val id: Int = 0,
    val name: String,
    val color: String? = null,
    val price: Double? = null,
    val status: BeerStatus = BeerStatus.ACTIVE,
    val sortValue: Double = .0
)
