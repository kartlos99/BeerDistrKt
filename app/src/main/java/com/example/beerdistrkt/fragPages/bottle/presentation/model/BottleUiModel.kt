package com.example.beerdistrkt.fragPages.bottle.presentation.model

import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.bottle.domain.model.BottleStatus

data class BottleUiModel(
    val id: Int = 0,
    val name: String = String.empty(),
    val volume: String = String.empty(),
    val beer: Beer? = null,
    val price: String = String.empty(),
    val status: BottleStatus = BottleStatus.ACTIVE,
    val sortValue: Double = .0,
    val imageFileName: String? = null,
)
