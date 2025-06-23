package com.example.beerdistrkt.fragPages.homePage.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.common.model.BarrelDto
import com.example.beerdistrkt.fragPages.beer.data.model.BeerDto
import com.example.beerdistrkt.fragPages.bottle.data.model.BottleDto

@Keep
data class BaseDataDto(
    val beers: List<BeerDto>,
    val bottles: List<BottleDto>,
    val barrels: List<BarrelDto>,
)
