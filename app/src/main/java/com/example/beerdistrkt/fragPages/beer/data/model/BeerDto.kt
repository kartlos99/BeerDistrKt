package com.example.beerdistrkt.fragPages.beer.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.models.BeerStatus

@Keep
data class BeerDto(
    var id: Int = 0,
    var name: String,
    var color: String? = null,
    var price: Double? = null,
    var status: BeerStatus = BeerStatus.ACTIVE,
    var sortValue: Double = .0
)
