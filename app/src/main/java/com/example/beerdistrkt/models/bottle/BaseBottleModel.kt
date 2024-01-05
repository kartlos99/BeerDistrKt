package com.example.beerdistrkt.models.bottle

import com.example.beerdistrkt.models.BeerModelBase

data class BaseBottleModel(
    val id: Int,
    val name: String,
    val volume: Double,
    val actualVolume: Double,
    val beer: BeerModelBase,
    val price: Double,
    val status: BottleStatus,
    val sortValue: String,
    val imageLink: String?
) {
    val isActive: Boolean
        get() = status == BottleStatus.ACTIVE
}
