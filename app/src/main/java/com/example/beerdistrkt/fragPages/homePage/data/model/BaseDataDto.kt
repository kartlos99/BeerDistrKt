package com.example.beerdistrkt.fragPages.homePage.data.model

import com.example.beerdistrkt.common.model.BarrelDto
import com.example.beerdistrkt.fragPages.beer.data.model.BeerDto
import com.example.beerdistrkt.models.bottle.dto.BaseBottleModelDto

data class BaseDataDto(
    val beers: List<BeerDto>,
    val bottles: List<BaseBottleModelDto>,
    val barrels: List<BarrelDto>,
)
